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
    logger.info("Closing Redis connection")
    redis_client.close()
    if exit_code == 0:
        logger.info("Exiting with code 0 (success)")
    else:
        logger.error("Exiting with code " + str(exit_code) + " (error)")
    exit(exit_code)

log_console_handler = logging.StreamHandler()
log_console_handler.setFormatter(logging.Formatter("[%(asctime)s %(levelname)s] %(name)s: %(message)s",datefmt="%Y-%m-%d %H:%M:%S"))

logger = logging.getLogger("controller")
logger.setLevel("INFO")
logger.addHandler(log_console_handler)

logger.info("Loggers initialized")

with open("config.yml","r") as fh:
    config = yaml.safe_load(fh)

chan_prefix = config["communication"]["channel-prefix"]

# the identifier that is used to differentiate this node on the network
# see docs section: Service Identifier
service_id = "controller"

logger.info("Loaded configuration")

# service status tracking
services = {}

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
        redis_client.publish("tb-controller-ping",create_message(service_id,message.sender,"controller-pong",None))

def handle_message_tb_controller_calls(message):
    pass

def handle_message_tb_service_status(message):
    if message.sender in services.keys():
        services[message.sender].set_status(message.data["online"])
    else:
        services[message.sender] = Service(message.sender,message.data["online"])
    logger.info("Service " + message.sender + " is now " + ("online" if message.data["online"] else "offline"))

def message_handler_target():
    while True:
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

logger.info("Starting message handler thread")

message_handler_thread = threading.Thread(target=message_handler_target,name="Redis mesage handler",daemon=True)
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
