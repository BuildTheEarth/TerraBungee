# TerraBungee
# (C) 2020 Saghetti
# Controller: manages the TerraBungee network

print("Starting controller...")

import time
import yaml
import requests
import redis
import threading
import logging
import json
from tbmsg import *
from tblib import *
import traceback

class InstanceCreationError(Exception):
    pass

class ScaleError(Exception):
    pass

def tb_exit(exit_code):
    logger.info("Exiting TerraBungee")
    logger.info("Pushing status")
    redis_client.publish(chan_prefix + "tb-service-status",create_message(service_id,"*","service-status",{"online":False}))
    logger.info("Stopping threads")
    if "message_handler_thread" in globals():
        message_handler_thread.do_run = False
        message_handler_thread.join()
    logger.info("Closing Redis connection")
    redis_client.close()
    if exit_code == 0:
        logger.info("Exiting with code 0 (success)")
    else:
        logger.error("Exiting with code " + str(exit_code) + " (error)")
    exit(exit_code)

def print_message_details(message):
    logger.info("== Message detail dump ==")
    logger.info("Sender: " + message.sender)
    logger.info("Recipient: " + message.recipient)
    logger.info("Type: " + message.type)
    logger.info("Data: " + str(message.data))
    logger.info("== End message dump ==")

# controller variables
controller_network_vars = {}
instances = {}
waiting_nodes = []
fleets = {}

class RemoteInstance:
    def __init__(self,parent_node,instance_id,template):
        self.instance_id = instance_id
        self.parent_node = parent_node
        self.address = "0.0.0.0:0"
        self.port = 0
        self.host = "0.0.0.0"
        self.online = True
        self.template = template
        self.exists = True
        self.parent_fleet = None

    def __repr__(self):
        return "<Instance " + self.instance_id + " on node " + self.parent_node.service_id + ">"

    def get_id(self):
        return self.instance_id

    def get_address(self):
        return self.address

    def is_online(self):
        return self.online

    def exists(self):
        return self.exists

    def stop(self):
        if not self.exists: return
        if self.online:
            redis_client.publish(chan_prefix + "tb-service-calls",create_message(service_id,self.parent_node.service_id,"stop-instance",{"instance-id":self.instance_id}))
            self.online = False

    def start(self):
        if not self.exists: return
        if not self.online:
            redis_client.publish(chan_prefix + "tb-service-calls",create_message(service_id,self.parent_node.service_id,"start-instance",{"instance-id":self.instance_id}))
            self.online = True

    def delete(self):
        self.exists = False
        redis_client.publish(chan_prefix + "tb-service-calls",create_message(service_id,self.parent_node.service_id,"delete-instance",{
            "instance-id": self.instance_id
        }))
        self.parent_node.instances.pop(self.instance_id,None)

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
        redis_client.publish(chan_prefix + "tb-service-calls",create_message(service_id,self.service_id,"create-instance",{
            "template": template,
            "instance-id": instance_id
        }))
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
        # add or remouve instances from the fleet
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
                        inst = create_instance(instance_id,self.template,target_nodes=self.target_nodes)
                    else:
                        # code for scaling without target nodes
                        inst = create_instance(instance_id,self.template)
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

logger.info("Loggers initialized")

with open("config.yml","r") as fh:
    config = yaml.safe_load(fh)

chan_prefix = config["communication"]["channel-prefix"]
controller_network_vars["template-url"] = config["resources"]["template-url"]
waiting_nodes = config["nodes-list"]

# the identifier that is used to differentiate this node on the network
# see docs section: Service Identifier
service_id = "controller"

logger.info("Loaded configuration")

# service status tracking
services = {}

# nodes
# TODO: make nodes a subclass of Service, do nice OOP stuff
nodes = {}

logger.info("Attempting to connect to Redis...")
redis_client = redis.StrictRedis(host=config["redis"]["host"], port=config["redis"]["port"], db=0)
# ensure that redis actually connects
if not redis_client.ping():
    logger.error("Unable to ping Redis!")
    tb_exit(1)
logger.info("Established Redis connection to " + config["redis"]["host"] + ":" + str(config["redis"]["port"]))

redis_pubsub = redis_client.pubsub()
redis_pubsub.subscribe(chan_prefix + "tb-controller-ping")
redis_pubsub.subscribe(chan_prefix + "tb-instance-status")
redis_pubsub.subscribe(chan_prefix + "tb-controller-calls")
redis_pubsub.subscribe(chan_prefix + "tb-service-status")

def handle_message_tb_controller_ping(message):
    if message.type == "controller-ping":
        logger.debug("Received ping from service " + message.sender)
        redis_client.publish(chan_prefix + "tb-controller-ping",create_message(service_id,message.sender,"controller-pong",None))

def handle_message_tb_controller_calls(message):
    if message.type == "get-var":
        if controller_network_vars.get(message.data["var"]) == None:
            redis_client.publish(chan_prefix + "tb-controller-calls",create_message(service_id,message.sender,"var-value",{
                "found": False,
                "var": message.data["var"]
            }))
        else:
            redis_client.publish(chan_prefix + "tb-controller-calls",create_message(service_id,message.sender,"var-value",{
                "found": True,
                "var": message.data["var"],
                "value": controller_network_vars[message.data["var"]]
            }))
        return
    if message.type == "node-limits":
        if not message.sender in nodes.keys():
            logger.warning("Node sent limits but doesn't exist? Ignoring message")
            print_message_details(message)
        else:
            logger.info("Maximum instances for node " + message.sender + " is " + str(message.data["max-instances"]))
            nodes[message.sender].max_instances = message.data["max-instances"]

def handle_message_tb_service_status(message):
    if message.sender in services.keys():
        services[message.sender].set_status(message.data["online"])
    else:
        services[message.sender] = Service(message.sender,message.data["online"])
    if message.sender.startswith("node:"):
        if message.data["online"]:
            # node going online
            #print(message.sender,"node on")
            if message.sender in nodes.keys():
                logger.info("Node sent online status twice! This should not happen, ignore if debugging. Replacing node object")
                nodes[message.sender] = None
                nodes[message.sender] = Node(message.sender)
            else:
                #logger.info("Node now online!")
                nodes[message.sender] = Node(message.sender)
        else:
            #print(message.sender,"node off")
            # node going offline
            if message.sender in nodes.keys():
                nodes.pop(message.sender)
                #logger.info("Node now offline!")
    logger.info("Service " + message.sender + " is now " + ("online" if message.data["online"] else "offline"))
    if message.sender in waiting_nodes:
        # NOTE: maybe add a safeguard preventing initialize_network_target from running twice
        # it should never happen, but you never know
        waiting_nodes.remove(message.sender)
        if len(waiting_nodes) > 0:
            logger.info("Waiting for " + str(len(waiting_nodes)) + " node(s) before starting instances.")
            logger.info(" ".join(waiting_nodes))
        else:
            initialize_network_thread.start()
    # :)
    # - sern

def message_handler_target():
    current_thread = threading.currentThread()
    while getattr(current_thread,"do_run",True):
        message = redis_pubsub.get_message()
        if not message:
            time.sleep(0.01)
            continue
        if not message["type"] == "message":
            continue
        message_parsed = parse_message(message["data"])
        if message_parsed.sender == service_id:
            continue # ignore messages from self
        if not ((message_parsed.recipient == service_id) or (message_parsed.recipient == "*")):
            continue # ignore messages not directed to us
        try:
            if message["channel"] == (chan_prefix + "tb-controller-ping").encode("utf-8"):
                handle_message_tb_controller_ping(message_parsed)
            if message["channel"] == (chan_prefix + "tb-controller-calls").encode("utf-8"):
                handle_message_tb_controller_calls(message_parsed)
            if message["channel"] == (chan_prefix + "tb-service-status").encode("utf-8"):
                handle_message_tb_service_status(message_parsed)
        except Exception as e:
            logger.error("An error occurred inside the message handler!")
            print_message_details(message_parsed)
            for line in traceback.format_exc().split("\n"):
                logger.error(line)

def create_instance(instance_id,template,target_nodes=nodes.values()):
    # create instance on the least strained node
    # get nodes sorted by amount of free instance "slots"
    possible_nodes = sorted(target_nodes, key=lambda node: node.max_instances - len(node.instances),reverse=True)
    # try creating instances
    for node in possible_nodes:
        try:
            return node.create_instance(instance_id,template)
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
    logger.info("Done!")

logger.info("Starting message handler thread")

message_handler_thread = threading.Thread(target=message_handler_target,name="Redis message handler",daemon=True)
message_handler_thread.do_run = True
message_handler_thread.start()

initialize_network_thread = threading.Thread(target=initialize_network_target,name="Redis message handler",daemon=True)
initialize_network_thread.do_run = True

# broadcast controller status to network
redis_client.publish(chan_prefix + "tb-service-status",create_message(service_id,"*","service-status",{"online":True}))
logger.info("Pushed status to network")

logger.info("Done! TerraBungee controller now online.")

logger.info("Waiting for " + str(len(waiting_nodes)) + " node(s) before starting instances.")
logger.info(" ".join(waiting_nodes))

try:
    while True:
        time.sleep(0.1)
except KeyboardInterrupt:
    tb_exit(0)
