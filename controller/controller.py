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

# controller variables
controller_network_vars = {}
instances = {}

class RemoteInstance:
    def __init__(self,instance_id):
        self.instance_id = instance_id
        self.address = "0.0.0.0:0"
        self.parent_node = ""
        self.port = 0
        self.host = "0.0.0.0"
        self.online = True
        self.template = ""

    def get_id(self):
        return self.instance_id

    def get_address(self):
        return self.address

    def is_online(self):
        return self.online

    def set_online(self,online):
        self.online = online

class Node:
    def __init__(self,service_id):
        self.service_id = ""

    def create_instance(self,instance_id,template):
        redis_client.publish("tb-controller-ping",create_message(service_id,message.sender,"controller-pong",None))

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
    logger.error("Unable to ping Reids!")
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
                nodes[message.sender] = None
                #logger.info("Node now offline!")
    logger.info("Service " + message.sender + " is now " + ("online" if message.data["online"] else "offline"))
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
        if message["channel"] == (chan_prefix + "tb-controller-ping").encode("utf-8"):
            handle_message_tb_controller_ping(message_parsed)
        if message["channel"] == (chan_prefix + "tb-controller-calls").encode("utf-8"):
            handle_message_tb_controller_calls(message_parsed)
        if message["channel"] == (chan_prefix + "tb-service-status").encode("utf-8"):
            handle_message_tb_service_status(message_parsed)
        if message["channel"] == (chan_prefix + "tb-instance-status").encode("utf-8"):
            print(message_parsed.sender,message_parsed.recipient,message_parsed.type,message_parsed.data)

def create_instance(node,instance_id,template):
    # create instance on the least strained node
    pass

logger.info("Starting message handler thread")

message_handler_thread = threading.Thread(target=message_handler_target,name="Redis mesage handler",daemon=True)
message_handler_thread.do_run = True
message_handler_thread.start()

# broadcast controller status to network
redis_client.publish(chan_prefix + "tb-service-status",create_message(service_id,"*","service-status",{"online":True}))
logger.info("Pushed status to network")

logger.info("Done! TerraBungee controller now online.")

try:
    while True:
        time.sleep(0.1)
except KeyboardInterrupt:
    tb_exit(0)
