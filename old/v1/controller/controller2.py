# TerraBungee
# (C) 2020 Saghetti
# Controller: manages the TerraBungee network

print("Starting controller...")

import time
import yaml
import requests
from flask import *
import threading
import logging
import json
from tbmsg import *
from tblib import *
import traceback
from flask.logging import default_handler
from werkzeug.exceptions import HTTPException

class InstanceCreationError(Exception):
    pass

class ScaleError(Exception):
    pass

def tb_exit(exit_code):
    logger.info("Exiting TerraBungee")
    for k, v in service_to_address_map.items():
        logger.info("Shutting down " + k)
        resp = requests.post(v + "api/shutdown",json= {
            "reason": "Controller shutdown"
        })
    if exit_code == 0:
        logger.info("Exiting with code 0 (success)")
    else:
        logger.error("Exiting with code " + str(exit_code) + " (error)")
    exit(exit_code)

# controller variables
controller_network_vars = {}
instances = {}
waiting_nodes = []
fleets = {}
service_to_address_map = {}

class RemoteInstance:
    def __init__(self,parent_node,instance_id,template):
        self.instance_id = instance_id
        self.parent_node = parent_node
        self.address = None
        self.running = True
        self.online = False
        self.template = template
        self.exists = True
        self.parent_fleet = None
        self.tags = {}

    def __repr__(self):
        return "<Instance " + self.instance_id + " on node " + self.parent_node.service_id + ">"

    def get_id(self):
        return self.instance_id

    def get_address(self):
        return self.address

    def is_online(self):
        return self.running

    def exists(self):
        return self.exists

    def start(self):
        if not self.exists: return
        if not self.running:
            resp = requests.post(service_to_address_map[self.parent_node.service_id] + "api/instances/" + self.instance_id + "/start")
            self.running = True

    def stop(self):
        if not self.exists: return
        if self.running:
            resp = requests.post(service_to_address_map[self.parent_node.service_id] + "api/instances/" + self.instance_id + "/stop")
            resp.raise_for_status()
            self.running = False
        self.address = None

    def delete(self):
        if not self.exists: return
        self.exists = False
        resp = requests.delete(service_to_address_map[self.parent_node.service_id] + "api/instances/" + self.instance_id)
        resp.raise_for_status()
        self.parent_node.instances.pop(self.instance_id,None)
        instances.pop(self.instance_id,None)

    def reprepare(self):
        if not self.exists: return
        resp = requests.post(service_to_address_map[self.parent_node.service_id] + "api/instances/" + self.instance_id + "/reprepare")
        resp.raise_for_status()

    def kill(self):
        if not self.exists: return
        resp = requests.post(service_to_address_map[self.parent_node.service_id] + "api/instances/" + self.instance_id + "/kill")
        self.running = False
        resp.raise_for_status()

class Node:
    def __init__(self,service_id):
        self.service_id = service_id
        self.instances = {}
        self.max_instances = -1

    def __repr__(self):
        return "<Node " + self.service_id + " with " + str(len(instances)) + " instance(s)>"

    def create_instance(self,instance_id,template):
        if self.max_instances > -1:
            if len(self.instances) >= self.max_instances:
                raise InstanceCreationError("No capacity to create a new instance")
        resp = requests.post(service_to_address_map[self.service_id] + "api/instances/" + instance_id,json={
            "template": template
        })
        resp.raise_for_status()
        self.instances[instance_id] = RemoteInstance(self,instance_id,template)
        return self.instances[instance_id]

    def get_instances(self):
        return self.instances.values()

class Fleet:
    def __init__(self,name,template,instance_naming_convention,target_nodes=[],max_instances=-1,load_balancer="fill"):
        self.name = name
        self.template = template
        self.target_nodes = target_nodes
        self.max_instances = max_instances
        self.load_balancer_mode = load_balancer
        self.max_instances = max_instances
        self.instances = []
        self.fleet_instance_counter = 1
        self.instance_naming_convention = instance_naming_convention

    def __repr__(self):
        return "<Fleet " + self.name + " with " + str(len(self.instances)) + " instance(s)>"

    def scale(self,amount):
        # add or remove instances from the fleet
        # returns the instances created
        if amount == 0:
            return # nothing to do
        if amount < 0:
            # TODO: implement
            raise RuntimeError("Downscaling a fleet is not supported yet")
        instances_added = []
        if amount > 0:
            for x in range(amount):
                instance_id = self.instance_naming_convention.replace("{NUM}",str(self.fleet_instance_counter))
                try:
                    if len(self.target_nodes) > 0:
                        # code for scaling with target nodes
                        inst = create_instance_load_balancing(instance_id,self.template,target_nodes=self.target_nodes)
                    else:
                        # code for scaling without target nodes
                        inst = create_instance_load_balancing(instance_id,self.template)
                    inst.parent_fleet = self
                    instances_added.append(inst)
                except InstanceCreationError:
                    pass
                self.fleet_instance_counter += 1
            return instances_added

log_console_handler = logging.StreamHandler()
log_console_handler.setFormatter(logging.Formatter("[%(asctime)s %(levelname)s] %(name)s: %(message)s",datefmt="%Y-%m-%d %H:%M:%S"))

logger = logging.getLogger("controller")
logger.setLevel("INFO")
logger.addHandler(log_console_handler)

flask_logger = logging.getLogger("flask")
flask_logger.setLevel("INFO")
flask_logger.addHandler(log_console_handler)

logger.info("Loggers initialized")

with open("config.yml","r") as fh:
    config = yaml.safe_load(fh)

controller_network_vars["template-url"] = config["resources"]["template-url"]
waiting_nodes = config["nodes-list"]

logger.info("Loaded configuration")

# the identifier that is used to differentiate this node on the network
# see docs section: Service Identifier
service_id = "controller"

nodes = {}

logger.info("Starting HTTP server...")

app = Flask(__name__)
app.logger = flask_logger

@app.errorhandler(HTTPException)
def handle_exception(e):
    return jsonify({
        "success": False,
        "error_code": e.code,
        "error_description": e.name
    }), e.code

# API calls

@app.route("/api/ping")
def route_api_ping():
    return jsonify({
        "time": time.time()
    })

@app.route("/api/variable/<var>")
def route_api_variable(var):
    if not controller_network_vars.get(var):
        abort(404)
    return jsonify({
        "value": controller_network_vars[var]
    })

@app.route("/api/services/<service_id>",methods=["GET","POST","DELETE"])
def route_api_service(service_id):
    if request.method == "GET":
        if not service_to_address_map.get(service_id):
            abort(404)
        return jsonify({
            "address": service_to_address_map.get(service_id)
        })
    if request.method == "POST":
        if not request.json:
            abort(400)
        if not request.json.get("url"):
            abort(400)
        if service_id in service_to_address_map.keys():
            abort(409)
        service_to_address_map[service_id] = request.json["url"]
        logger.info("Service " + service_id + " now online")
        if service_id.startswith("node:"):
            if not (service_id in nodes.keys()):
                nodes[service_id] = Node(service_id)
        if service_id in waiting_nodes:
            # NOTE: maybe add a safeguard preventing initialize_network_target from running twice
            # it should never happen, but you never know
            waiting_nodes.remove(service_id)
            if len(waiting_nodes) > 0:
                logger.info("Waiting for " + str(len(waiting_nodes)) + " node(s) before starting instances.")
                logger.info(" ".join(waiting_nodes))
            else:
                initialize_network_thread.start()
        return ("", 201)
    if request.method == "DELETE":
        if not service_to_address_map.get(service_id):
            abort(404)
        service_to_address_map.pop(service_id,None)
        logger.info("Service " + service_id + " now offline")
        if service_id in nodes.keys():
            nodes.pop(service_id,None)
        return ("", 204)

@app.route("/api/instances/",methods=["GET"])
def route_api_instances():
    return_data = []
    for instance_id, instance_object in instances.items():
        instance_serialized_data = {
            "id": instance_id,
            "template": instance_object.template,
            "running": instance_object.running,
            "online": instance_object.online,
        }
        if instance_object.address:
            instance_serialized_data["address"] = instance_object.address
        if instance_object.parent_fleet:
            instance_serialized_data["fleet"] = instance_object.parent_fleet.name
        return_data.append(instance_serialized_data)
    return jsonify(return_data)

@app.route("/api/instances/<instance_id>",methods=["GET"])
def route_api_instances_specific(instance_id):
    if request.method == "GET":
        if not instance_id in instances.keys():
            abort(404)
        instance_object = instances[instance_id]
        instance_serialized_data = {
            "id": instance_id,
            "template": instance_object.template,
            "running": instance_object.running,
            "online": instance_object.online,
        }
        if instance_object.address:
            instance_serialized_data["address"] = instance_object.address
        if instance_object.parent_fleet:
            instance_serialized_data["fleet"] = instance_object.parent_fleet.name
        return jsonify(instance_serialized_data)
    if request.method == "POST":
        if not request.json:
            abort(400)
        if instance_id in instances.keys():
            abort(409)
        if not request.json.get("template"):
            abort(400)
        if request.json.get("nodes"):
            pass

@app.route("/api/instances/<instance_id>/start",methods=["POST"])
def route_api_instances_start(instance_id):
    inst = instances.get(instance_id)
    if not inst:
        abort(404)
    inst.start()
    return "", 204

@app.route("/api/instances/<instance_id>/stop",methods=["POST"])
def route_api_instances_stop(instance_id):
    inst = instances.get(instance_id)
    if not inst:
        abort(404)
    inst.stop()
    return "", 204

@app.route("/api/instances/<instance_id>/kill",methods=["POST"])
def route_api_instances_kill(instance_id):
    inst = instances.get(instance_id)
    if not inst:
        abort(404)
    inst.kill()
    return "", 204

@app.route("/api/instances/<instance_id>/reprepare",methods=["POST"])
def route_api_instances_reprepare(instance_id):
    inst = instances.get(instance_id)
    if not inst:
        abort(404)
    inst.reprepare()
    return "", 204

# push endpoints

@app.route("/push/instance/online",methods=["POST"])
def route_push_instance_online():
    if not request.json:
        abort(400)
    if not request.json.get("instance_id"):
        abort(400)
    if not request.json.get("address"):
        abort(400)
    if not instances.get(request.json["instance_id"]):
        abort(404)
    if not instances[request.json["instance_id"]].online:
        instances[request.json["instance_id"]].online = True
        instances[request.json["instance_id"]].address = request.json["address"]
        logger.info("Instance " + request.json["instance_id"] + " now online on address " + request.json["address"])
    else:
        logger.warning("Instance " + request.json["instance_id"] + " came online but was already online?!")
    return "", 204

@app.route("/push/instance/offline",methods=["POST"])
def route_push_instance_offline():
    if not request.json:
        abort(400)
    if not request.json.get("instance_id"):
        abort(400)
    if not instances.get(request.json["instance_id"]):
        abort(404)
    if instances[request.json["instance_id"]].online:
        instances[request.json["instance_id"]].online = False
        instances[request.json["instance_id"]].address = None
        logger.info("Instance " + request.json["instance_id"] + " is now offline")
    else:
        logger.warning("Instance " + request.json["instance_id"] + " went offline but was already offline?!")
    return "", 204

# more functions

def create_instance_load_balancing(instance_id,template,target_nodes=nodes.values()):
    # create instance on the least strained node
    # get nodes sorted by amount of free instance "slots"
    possible_nodes = sorted(target_nodes, key=lambda node: node.max_instances - len(node.instances),reverse=True)
    # try creating instances
    for node in possible_nodes:
        try:
            instance = node.create_instance(instance_id,template)
            instances[instance_id] = instance
            return instance
        except InstanceCreationError:
            continue # no slots available
    # couldn't create the instance!
    raise InstanceCreationError("No capacity to create a new instance")

def initialize_network_target():
    logger.info("Initializing network...")
    for fleet_id,fleet_config_data in config["fleets"].items():
        logger.info("Starting fleet " + fleet_id)
        target_nodes = []
        if len(fleet_config_data["target-nodes"]) > 0:
            # try and get target nodes
            for target_node_id in fleet_config_data["target-nodes"]:
                if not nodes.get(target_node_id):
                    logger.error("Unable to get node by the name of " + target_node_id + "! Is it online?")
                else:
                    target_nodes.append(nodes[target_node_id])
        if len(target_nodes) > 0:
            logger.info("Target nodes for fleet " + fleet_id + ": " + str(target_nodes))
            fleets[fleet_id] = Fleet(fleet_id,fleet_config_data["template"],fleet_config_data["instance-naming-convention"],load_balancer=fleet_config_data["load-balancer"],max_instances=fleet_config_data["max-instances"],target_nodes=target_nodes)
        else:
            logger.info("Fleet " + fleet_id + " does not have any target nodes.")
            fleets[fleet_id] = Fleet(fleet_id,fleet_config_data["template"],fleet_config_data["instance-naming-convention"],load_balancer=fleet_config_data["load-balancer"],max_instances=fleet_config_data["max-instances"])
        fleet = fleets[fleet_id]
        logger.info("Starting " + str(fleet_config_data["min-instances"]) + " instance(s) for fleet " + fleet_id)
        created_instances = fleet.scale(fleet_config_data["min-instances"])
        if len(created_instances) < fleet_config_data["min-instances"]:
            logger.warning("Unable to create all instances for fleet " + fleet_id + "! Only " + str(len(created_instances)) + " were created.")
        #print("Stopping",created_instances[0])
        #created_instances[0].stop()
    logger.info("Done!")

initialize_network_thread = threading.Thread(target=initialize_network_target,name="Network initializer thread",daemon=True)

logger.info("Done! TerraBungee controller now online.")

logger.info("Waiting for " + str(len(waiting_nodes)) + " node(s) before starting instances.")
logger.info(" ".join(waiting_nodes))

logging.getLogger('werkzeug').setLevel("INFO")

if __name__ == "__main__":
    app.run(host=config["http"]["host"],port=config["http"]["port"])
tb_exit(0)
