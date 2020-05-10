# TerraBungee
# (C) 2020 Saghetti
# Node: manages server instances

print("Starting node...")

import time
import yaml
import requests
import redis
import threading
import logging
import json
import socket
from tbmsg import *
import socket
import subprocess

# utility functions

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
    logger.info("Closing Redis connection")
    redis_client.close()
    if exit_code == 0:
        logger.info("Exiting with code 0 (success)")
    else:
        logger.error("Exiting with code " + str(exit_code) + " (error)")
    exit(exit_code)

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

def handle_message_tb_service_calls(message):
    print(message)

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
        if message["channel"] == (chan_prefix + "tb-service-calls").encode("utf-8"):
            handle_message_tb_service_calls(message_parsed)

logger.info("Starting message handler thread")

message_handler_thread = threading.Thread(target=message_handler_target,name="Redis mesage handler",daemon=True)
message_handler_thread.start()

# broadcast status to network
redis_client.publish(chan_prefix + "tb-service-status",create_message(service_id,"*","service-status",{"online":True}))
logger.info("Pushed status to network")

logger.info("Done! TerraBungee service " + service_id + " now online.")

try:
    while True:
        time.sleep(0.1)
except KeyboardInterrupt:
    tb_exit(0)
