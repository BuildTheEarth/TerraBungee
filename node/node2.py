# TerraBungee
# (C) 2020 Saghetti
# Node: manages server instances

print("Starting node...")

import time
import yaml
import urllib.request
from flask import *
import threading
import logging
import json
import socket
import socket
import subprocess
import zipfile
import shutil
import os
import queue
import traceback
import requests
from werkzeug.exceptions import HTTPException
import signal

# var definitions
file_server_url = "http://0.0.0.0/" # default URL, make sure new one is set
instances = {}
instance_queue = queue.Queue()
used_ports = [] # keep track of used ports
config = {}
hostname = "localhost"

# utility functions

def download_file(url,dest):
    response = urllib.request.urlopen(url)
    with open(dest,"wb") as fh:
        fh.write(response.read())

def get_free_port():
    s = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
    s.bind(("127.0.0.1",0))
    port = 0
    try:
        port = s.getsockname()[1]
    finally:
        s.close()
    return port

def tb_exit(exit_code):
    # stop terrabungee gracefully
    logger.info("Exiting TerraBungee")
    logger.info("Pushing status")
    try:
        requests.delete(controller_url + "api/services/" + service_id)
    except requests.exceptions.ConnectionError:
        logger.error("Unable to push offline status to controller!")
    logger.info("Shutting down instances")
    for instance_id, instance in instances.items():
        logger.info("Stopping " + instance_id)
        instance.stop()
        instance.delete()
    logger.info("Cleaning up files")
    clean_folders()
    if exit_code == 0:
        logger.info("Exiting with code 0 (success)")
    else:
        logger.error("Exiting with code " + str(exit_code) + " (error)")
    exit(exit_code)

def clean_folders():
    # clean instance folder
    # CAUTION: DO NOT DO THIS WHILE INSTANCES ARE RUNNING
    if os.path.exists("instances/"):
        shutil.rmtree("instances/")
    os.makedirs("instances/")
    open("instances/DUMMY","w").close()
    # clean temp using same method
    if os.path.exists("temp/"):
        shutil.rmtree("temp/")
    os.makedirs("temp/")
    open("temp/DUMMY","w").close()

class InstancePrepareError(Exception):
    pass

class Instance:
    def __init__(self,instance_id,template):
        self.instance_id = instance_id
        self.instance_folder = "instances/" + self.instance_id
        self.prepared = False
        self.running = False
        self.process = None
        self.server_settings = None
        self.port = 0
        self.address = None
        self.template = template
        self.service_id = "instance:" + instance_id

    def __repr__(self):
        if self.running:
            return "<Instance " + self.instance_id + " on address " + self.address + ">"
        else:
            return "<Instance " + self.instance_id + ">"

    def prepare(self,template_path):
        if self.prepared:
            return
        # prepare an instance for launching
        os.makedirs("temp/" + self.instance_id)
        # extract json from zip
        template_zip = zipfile.ZipFile(template_path,"r")
        template_zip.extractall(path="temp/" + self.instance_id)
        # parse json
        with open("temp/" + self.instance_id + "/template.json","r") as fh:
            template_json = json.loads(fh.read())
        self.server_settings = template_json["server-settings"]
        # apply overrides and create instance folder
        shutil.move("temp/" + self.instance_id + "/overrides", self.instance_folder)
        # do actions specified in template json
        for action in template_json["actions"]:
            if action["action"] == "download":
                download_file(file_server_url + action["source"],self.instance_folder + "/" + action["dest"])
            elif action["action"] == "create-folder":
                os.makedirs(self.instance_folder + "/" + action["dest"])
            elif action["action"] == "unpack":
                download_file(file_server_url + action["source"],"temp/" + self.instance_id + "/" + action["source"])
                with zipfile.ZipFile("temp/" + self.instance_id + "/" + action["source"]) as sub_zip:
                    # unpack files
                    sub_zip.extractall(path=self.instance_folder + "/" + action["dest"])
            else:
                raise InstancePrepareError("Unknown action type " + action["action"] + " while preparing " + instance_id)
        # create instance info files
        os.makedirs(self.instance_folder + "/tb_info")
        with open(self.instance_folder + "/tb_info/id.txt","w") as fh:
            fh.write(self.instance_id)
        with open(self.instance_folder + "/tb_info/controllerurl.txt","w") as fh:
            fh.write(config["controller-url"])
        # clean up
        shutil.rmtree("temp/" + self.instance_id)
        template_zip.close()
        self.prepared = True

    def start(self):
        # start the instance
        if self.running:
            return
        if not self.prepared:
            return
        # find a free port
        # CAUTION: can cause a race condition if another program uses the port!
        found_port = False
        while not found_port:
            port = get_free_port()
            if port in used_ports:
                continue
            used_ports.append(port)
            found_port = True
            self.port = port
        # write port to server.properties
        file_data = ""
        with open(self.instance_folder + "/server.properties","r") as fh:
            file_data = fh.read()
            file_data = file_data.replace("{port}",str(self.port))
        with open(self.instance_folder + "/server.properties","w") as fh:
            fh.write(file_data)
        self.address = hostname + ":" + str(self.port)
        # write address
        with open(self.instance_folder + "/tb_info/address.txt","w") as fh:
            fh.write(self.address)
        # send message to controller
        # start the server
        self.process = subprocess.Popen(
            "java -Xmx" + self.server_settings["ram"] + " " +
            self.server_settings["jvm-args"] +
            " -jar " + self.server_settings["server-jar"],
            cwd=self.instance_folder,
            stdin=subprocess.PIPE, # not sure what difference this makes, might leave it uncommented if it doesn't cause any issues
            # seems like it makes process.communicate() actually work
            # redirect input to /dev/null (or os equivelent)
            stdout=subprocess.PIPE, # note: may add better logging support later
            stderr=subprocess.PIPE, # TODO: make better
        )
        #print("Server running on " + self.address)
        self.running = True

    def stop(self):
        # shut down the instance process
        if not self.running:
            return
        if not self.prepared:
            return
        # you can see here where i tried a ton of different methods to shut down instances
        #self.process.communicate(b"stop\r\n")
        #self.process.kill()
        #self.process.send_signal(signal.SIGTERM)
        self.process.communicate(b"stop\n")
        #self.process.wait()
        # remove port from list of used ports
        used_ports.remove(self.port)
        self.running = False
        self.address = None

    def delete(self):
        # delete the instance
        if self.running:
            # stop if it's running, just in case
            self.stop()
        # delete instance dir
        #shutil.rmtree("instances/" + self.instance_id)
        self.prepared = False # note: allows for instance reuse, not sure if this is a good idea

def create_new_instance(instance_id,template):
    logger.info("Creating instance " + instance_id + " with template " + template)
    if instances.get(instance_id):
        logger.error("Instance ID already exists?! Ignoring request to create instance")
        return instances.get(instance_id)
    # download template
    #print(file_server_url)
    download_file(file_server_url + "templates/" + template + ".zip","temp/" + template + ".zip")
    # create instance
    inst = Instance(instance_id,template)
    instances[instance_id] = inst
    # prepare instance
    inst.prepare("temp/" + template + ".zip")
    # start instance
    inst.start()
    # delete template
    os.remove("temp/" + template + ".zip")
    logger.info("Instance " + instance_id + " has been created")
    # TODO: push new instance status to controller (possibly)
    return inst

log_console_handler = logging.StreamHandler()
log_console_handler.setFormatter(logging.Formatter("[%(asctime)s %(levelname)s] %(name)s: %(message)s",datefmt="%Y-%m-%d %H:%M:%S"))

logger = logging.getLogger("node")
logger.setLevel("INFO")
logger.addHandler(log_console_handler)

flask_logger = logging.getLogger("flask")
flask_logger.setLevel("INFO")
flask_logger.addHandler(log_console_handler)

logger.info("Loggers initialized")

with open("config.yml","r") as fh:
    config = yaml.safe_load(fh)

node_name = config["node-name"]
hostname = config["hostname"]
controller_url = config["controller-url"]

# the identifier that is used to differentiate this node on the network
# see docs section: Service Identifier
service_id = "node:" + node_name

logger.info("Loaded configuration")
logger.info("Pinging controller...")
try:
    response = requests.get(controller_url + "api/ping")
except requests.exceptions.ConnectionError:
    logger.error("Unable to ping controller!")
    tb_exit(1)
if response.status_code != 200:
    logger.error("Controller returned status code " + str(response.status_code) + "!")
    tb_exit()

logger.info("Checking latency...")
start_time = time.time()
for x in range(5):
    response = requests.get(controller_url + "api/ping",params={"latency_test":True})
time_taken = time.time() - start_time
logger.info("Average latency of " + str(round((time_taken/5)*1000,2)) + "ms")

logger.info("Starting HTTP server...")

app = Flask(__name__)
app.logger = flask_logger

@app.errorhandler(HTTPException)
def handle_exception(e):
    return jsonify({
        "error": True,
        "error_code": e.code,
        "error_description": e.name
    }), e.code

# API calls

@app.route("/api/ping")
def route_api_ping():
    return jsonify({
        "time": time.time()
    })

@app.route("/api/instances/")
def route_api_instances():
    return_data = []
    for instance_id, instance_object in instances.items():
        instance_serialized_data = {
            "id": instance_id,
            "template": instance_object.template,
            "running": instance_object.running,
        }
        if instance_object.address:
            instance_serialized_data["address"] = instance_object.address
        return_data.append(instance_serialized_data)
    return jsonify(return_data)

@app.route("/api/instances/<instance_id>",methods=["GET","POST","PATCH","DELETE"])
def route_api_instances_specific(instance_id):
    if request.method == "GET":
        inst = instances.get(instance_id)
        if not inst:
            abort(404)
        instance_serialized_data = {
            "id": inst,
            "template": inst.template,
            "online": inst.running,
        }
        if instance_object.address:
            instance_serialized_data["address"] = inst.address
        return jsonify(instance_serialized_data)
    if request.method == "POST":
        if not request.json:
            abort(400)
        if not request.json.get("template"):
            abort(400)
        if instance_id in instances.keys():
            abort(409)
        create_new_instance(instance_id,request.json.get("template"))
        return ("",201)
    if request.method == "PATCH":
        inst = instances.get(instance_id)
        if not inst:
            abort(404)
        if not request.json:
            abort(400)
        if request.json.get("running") != None:
            if request.json["running"]:
                inst.delete()
                instances[inst].pop(instance_id,None)
        return "", 204
    if request.method == "DELETE":
        inst = instances.get(instance_id)
        if not inst:
            abort(404)
        inst.delete()
        instances[instance_id].pop(instance_id,None)
        return "", 204

@app.route("/api/shutdown",methods=["POST"])
def route_api_shutdown():
    if request.method == "POST":
        if not request.json:
            abort(400)
        if not request.json.get("reason"):
            abort(400)
        if not request.environ.get('werkzeug.server.shutdown'):
            abort(500)
        logger.info("Starting shutdown due to API call...")
        logger.info("Reason: " + request.json.get("reason"))
        request.environ.get('werkzeug.server.shutdown')()
        return ("", 204)

logger.info("Getting file provider URL")
resp = requests.get(controller_url + "api/variable/template-url")
if resp.status_code != 200:
    logger.error("Controller returned non-200 response!")
    tb_exit(1)
file_server_url = resp.json()["value"]
logger.info("File provider URL is " + file_server_url)

logger.info("Cleaning up files")
clean_folders()

logger.info("Pushing status to controller")
requests.post(controller_url + "api/services/" + service_id,json={
    "url": "http://" + hostname + ":" + str(config["http"]["port"]) + "/"
})

logging.getLogger('werkzeug').setLevel("WARN")

"""
def instance_output_target():
    while True:
        for instance_id, instance in instances.items():
            if not instance.process:
                continue
            print_text = instance.process.stdout.readline()
            while print_text:
                print(instance_id + ": " + print_text.decode("utf-8"),end="")
                print_text = instance.process.stdout.readline()

instance_output_thread = threading.Thread(name="Instance output thread",daemon=True,target=instance_output_target)
instance_output_thread.start()
"""

if __name__ == "__main__":
    app.run(host=config["http"]["host"],port=config["http"]["port"])
tb_exit(0)
