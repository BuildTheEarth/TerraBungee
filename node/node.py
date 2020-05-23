# TerraBungee
# (C) 2020 Saghetti
# Node: manages server instances

print("Starting node...")

import time
import yaml
import urllib.request
import redis
import threading
import logging
import json
import socket
from tbmsg import *
import socket
import subprocess
import zipfile
import shutil
import os
import queue

# var definitions
file_server_url = "http://127.0.0.1/" # default URL, make sure new one is set
instances = {}
instance_queue = queue.Queue()

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
    logger.info("Exiting TerraBungee")
    logger.info("Pushing status")
    redis_client.publish(chan_prefix + "tb-service-status",create_message(service_id,"*","service-status",{"online":False}))
    logger.info("Stopping threads")
    if "message_handler_thread" in globals():
        message_handler_thread.do_run = False
        message_handler_thread.join()
    if "instance_creation_thread" in globals():
        instance_creation_thread.do_run = False
        instance_creation_thread.join()
    logger.info("Shutting down instances")
    for instance_id, instance in instances.items():
        logger.info("Stopping " + instance_id)
        instance.stop()
        instance.delete()
    logger.info("Closing Redis connection")
    redis_client.close()
    if exit_code == 0:
        logger.info("Exiting with code 0 (success)")
    else:
        logger.error("Exiting with code " + str(exit_code) + " (error)")
    exit(exit_code)

class InstancePrepareError(Exception):
    pass

class Instance:
    def __init__(self,instance_id):
        self.instance_id = instance_id
        self.instance_folder = "instances/" + self.instance_id
        self.prepared = False
        self.running = False

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
        # create id.txt
        with open(self.instance_folder + "/id.txt","w") as fh:
            fh.write(self.instance_id)
        # clean up
        shutil.rmtree("temp/" + self.instance_id)
        template_zip.close()
        self.prepared = True

    def start(self):
        # start the instance
        # TODO: implement
        if self.running:
            return
        self.running = True

    def stop(self):
        # shut down the instance process
        # TODO: implement
        if not self.running:
            return
        self.running = False

    def delete(self):
        # delete the instance
        if self.running:
            # stop if it's running, just in case
            self.stop()
        # delete instance dir
        shutil.rmtree("instances/" + self.instance_id)

class InstanceCreateTask:
    def __init__(self,instance_id,template):
        self.instance_id = instance_id
        self.template = template

log_console_handler = logging.StreamHandler()
log_console_handler.setFormatter(logging.Formatter("[%(asctime)s %(levelname)s] %(name)s: %(message)s",datefmt="%Y-%m-%d %H:%M:%S"))

logger = logging.getLogger("node")
logger.setLevel("INFO")
logger.addHandler(log_console_handler)

logger.info("Loggers initialized")

with open("config.yml","r") as fh:
    config = yaml.safe_load(fh)

chan_prefix = config["communication"]["channel-prefix"]
node_name = config["node-name"]

# the identifier that is used to differentiate this node on the network
# see docs section: Service Identifier
service_id = "node:" + node_name

logger.info("Loaded configuration")

logger.info("Attempting to connect to Redis...")
redis_client = redis.StrictRedis(host=config["redis"]["host"], port=config["redis"]["port"], db=0)
# ensure that redis actually connects
if not redis_client.ping():
    logger.error("Unable to ping Reids!")
    tb_exit(1)
logger.info("Established Redis connection to " + config["redis"]["host"] + ":" + str(config["redis"]["port"]))

redis_pubsub = redis_client.pubsub()
redis_pubsub.subscribe(chan_prefix + "tb-controller-ping")

# initialize redis

logger.info("Pinging controller")

redis_client.publish(chan_prefix + "tb-controller-ping",create_message(service_id,"controller","controller-ping",None))

# try and ping the controller
# extra variables here are for keeping track of how much time has elapsed

ping_trying = True
ping_start_time = time.time()
ping_time_elapsed = 0
ping_warned = False
while ping_trying:
    ping_time_elapsed = time.time() - ping_start_time
    message = redis_pubsub.get_message()
    if ping_time_elapsed > 5 and not ping_warned:
        logger.warning("Controller hasn't responded for 5 seconds! Is the controller overloaded or offline?")
        logger.warning("Resending message")
        redis_client.publish(chan_prefix + "tb-controller-ping",create_message(service_id,"controller","controller-ping",None))
        ping_warned = True
    if ping_time_elapsed > 10:
        logger.error("Controller hasn't responded for 10 seconds! Make sure that the controller is online and properly configured.")
        ping_trying = False
        tb_exit(1)
    if not message:
        time.sleep(0.01)
        continue
    if not message["type"] == "message":
        continue
    if not message["channel"] == (chan_prefix + "tb-controller-ping").encode("utf-8"):
        continue
    message_parsed = parse_message(message["data"])
    if message_parsed.sender == service_id:
        continue # ignore messages from self
    if message_parsed.sender != "controller":
        continue # listen only to messages from the controller
    if message_parsed.recipient != service_id:
        continue # the message was not sent to us
    if message_parsed.type == "controller-pong":
        # controller has responded with a pong
        ping_trying = False
        logger.info("Recieved response in " + str(round(ping_time_elapsed,2)) + " seconds")
        break

# start threads and enter mainloop

redis_pubsub.unsubscribe(chan_prefix + "tb-controller-ping")
redis_pubsub.subscribe(chan_prefix + "tb-instance-status")
redis_pubsub.subscribe(chan_prefix + "tb-service-calls")
redis_pubsub.subscribe(chan_prefix + "tb-controller-calls")

def create_new_instance(inst_id,template):
    # add a new task to the instance creation queue
    instance_queue.put(InstanceCreateTask(inst_id,template))

def handle_message_tb_service_calls(message):
    if message.type == "create-instance":
        # create a new instance
        create_new_instance(message.data["instance-id"],message.data["template"])

def handle_message_tb_controller_calls(message):
    if message.type == "var-value":
        if message.data["found"]:
            if message.data["var"] == "template-url":
                global file_server_url
                file_server_url = message.data["value"]

# target for message handling thread
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
        if message["channel"] == (chan_prefix + "tb-service-calls").encode("utf-8"):
            handle_message_tb_service_calls(message_parsed)
        if message["channel"] == (chan_prefix + "tb-controller-calls").encode("utf-8"):
            handle_message_tb_controller_calls(message_parsed)

# target for instance creation thread
def instance_creation_target():
    current_thread = threading.currentThread()
    while getattr(current_thread,"do_run",True):
        try:
            task = instance_queue.get(timeout=0.05)
            logger.info("Creating instance " + task.instance_id + " with template " + task.template)
            if instances.get(task.instance_id):
                logger.error("Instance ID already exists?! Ignoring request to create instance")
                continue
            # download template
            download_file(file_server_url + "templates/" + task.template + ".zip","temp/" + task.template + ".zip")
            # create instance
            inst = Instance(task.instance_id)
            instances[task.instance_id] = inst
            # prepare instance
            inst.prepare("temp/" + task.template + ".zip")
            # start instance
            inst.start()
            # delete template
            os.remove("temp/" + task.template + ".zip")
            logger.info("Instance " + task.instance_id + " has been created")
        except queue.Empty:
            continue

logger.info("Starting message handler thread")

message_handler_thread = threading.Thread(target=message_handler_target,name="Redis mesage handler",daemon=True)
message_handler_thread.do_run = True
message_handler_thread.start()

logger.info("Starting instance creation thread")

instance_creation_thread = threading.Thread(target=instance_creation_target,name="Instance creation thread",daemon=True)
instance_creation_thread.do_run = True
instance_creation_thread.start()

# broadcast status to network
redis_client.publish(chan_prefix + "tb-service-status",create_message(service_id,"*","service-status",{"online":True}))
logger.info("Pushed status to network")

# grab information from controller
logger.info("Requesting vars from controller")
redis_client.publish(chan_prefix + "tb-controller-calls",create_message(service_id,"controller","get-var",{"var":"template-url"}))

logger.info("Done! TerraBungee service " + service_id + " now online.")

# do tests
time.sleep(0.5)
create_new_instance("lobby-1","lobby")

try:
    while True:
        time.sleep(0.1)
except KeyboardInterrupt:
    tb_exit(0)
