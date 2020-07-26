# TerraBungee
# (C) 2020 Saghetti
# Network Debugger: listens for commands

# TOOD: support channel prefixes

print("Starting network debugger...")

import time
import yaml
import redis
import threading
from tbmsg import *
import logging
import random

all_channels = ["tb-controller-calls","tb-service-calls","tb-controller-ping","tb-service-status"]
service_id = "debugger-" + str(random.randint(1,9999))
currently_subscribed = []
waiting_responses_instances_list = [] # list of nodes that we're currently waiting for instances-list responses from
raw_redis_enabled = True

def command_subscribe(args):
    """Subscribes to the channels given. Use * to subscribe to all channels available."""
    if len(args) == 0:
        logger.info("Usage: .subscribe <channel OR *> [channel] ...")
        return
    chans_to_subscribe = []
    subscribing_all = False
    for itm in args:
        if itm == "*":
            subscribing_all = True
            break
        if not itm in all_channels:
            logger.error("Channel " + itm + " is not valid!")
            return
        if itm in currently_subscribed:
            logger.error("Already subscribed to " + itm + "!")
            return
        chans_to_subscribe.append(itm)
    if subscribing_all:
        for itm in all_channels:
            if itm in currently_subscribed:
                continue
            currently_subscribed.append(itm)
            redis_pubsub.subscribe(itm)
        logger.info("Subscribed to all channels")
        return
    else:
        for itm in chans_to_subscribe:
            currently_subscribed.append(itm)
            redis_pubsub.subscribe(itm)
    logger.info("Subscribed to channels " + " ".join(chans_to_subscribe))

def command_unsubscribe(args):
    """Unsubscribes from the channels given. Use * to unsubscribe from all."""
    if len(args) == 0:
        logger.info("Usage: .unsubscribe <channel OR *> [channel] ...")
        return
    chans_to_unsubscribe = []
    unsubscribing_all = False
    for itm in args:
        if itm == "*":
            unsubscribing_all = True
            break
        if not itm in all_channels:
            logger.error("Channel " + itm + " is not valid!")
            return
        if not (itm in currently_subscribed):
            logger.error("Not subscribed to " + itm + "!")
            return
        chans_to_unsubscribe.append(itm)
    if unsubscribing_all:
        for itm in all_channels:
            if not (itm in currently_subscribed):
                continue
            currently_subscribed.remove(itm)
            redis_pubsub.subscribe(itm)
        logger.info("Unsubscribed from all channels")
        return
    else:
        for itm in chans_to_unsubscribe:
            currently_subscribed.remove(itm)
            redis_pubsub.unsubscribe(itm)
    logger.info("Unsubscribed from channels " + " ".join(chans_to_unsubscribe))

def command_channels(args):
    """Lists the channels that the debugger is currently subscribed to."""
    logger.info("Currently subscribed to channels: " + " ".join(currently_subscribed))

def command_raw_redis(args):
    global raw_redis_enabled
    """Enables or disables raw Redis data logging. Useful for seeing the structure of messages. Use \".raw_redis on/off\""""
    if len(args) == 0:
        logger.info("Raw Redis data is currently " + ("enabled" if raw_redis_enabled else "disabled"))
        return
    if args[0] == "on":
        logger.info("Raw Redis data has been enabled")
        raw_redis_enabled = True
    if args[0] == "off":
        logger.info("Raw Redis data has been disabled")
        raw_redis_enabled = False

def command_help(args):
    """Views help for all commands. Use .help <command> to view help for a specific command."""
    if len(args) == 0:
        for k,v in commands.items():
            logger.info(k + ": " + (v.__doc__ if v.__doc__ != None else "No help available. Sorry!"))
        return
    cmd_function = commands.get(args[0])
    if cmd_function == None:
        logger.info("This command doesn't exist!")
    else:
        logger.info(args[0] + ": " + (cmd_function.__doc__ if cmd_function.__doc__ != None else "No help available. Sorry!"))

def command_commands_list(args):
    """Lists all of the commands available in the debugger."""
    logger.info("Commands: " + " ".join(commands.keys()))

def command_dummy_exit(args):
    """Exits the debugger"""
    pass
    # no code here because it's already implemented inside of the command loop

def command_send(args):
    """Sends a message to a service. Usage: send <channel> <recipient> <message type> <json data>"""
    if len(args) < 4:
        logger.info("Usage: send <channel> <recipient> <message type> <json data>")
        return
    channel = args[0]
    recipient = args[1]
    message_type = args[2]
    json_data = json.loads(" ".join(args[3:]))
    redis_client.publish(channel,create_message(service_id,recipient,message_type,json_data))
    logger.info("Sent message " + message_type + " to " + recipient + "!")

def command_get_instances(args):
    """Gets the raw instance list for a node. Usage: get-instances <node>"""
    if len(args) < 1:
        logger.info("Usage: get-instances <node>")
        return
    logger.info("Getting instances for " + args[0])
    waiting_responses_instances_list.append(args[0])
    redis_client.publish("tb-service-calls",create_message(service_id,args[0],"get-instances",None))

def command_create_instance(args):
    """Creates an instance. Usage: create-instance <instance-id> <template> [node]"""
    # TODO: implement automatic load balancing via service calls if "node" is not specified
    if len(args) < 3:
        logger.info("Usage: create-instance <instance-id> <template> [node]")
        return
    redis_client.publish("tb-service-calls",create_message(service_id,args[2],"create-instance",{
        "template": args[1],
        "instance-id": args[0]
    }))

def command_delete_instance(args):
    """Deletes an instance. Usage: delete-instance <instance-id> [node]"""
    # TODO: implement automatic node detection via service calls if "node" is not specified
    if len(args) < 2:
        logger.info("Usage: delete-instance <instance-id> [node]")
        return
    redis_client.publish("tb-service-calls",create_message(service_id,args[1],"delete-instance",{
        "instance-id": args[0]
    }))

commands = {
    ".subscribe": command_subscribe,
    ".unsubscribe": command_unsubscribe,
    ".channels": command_channels,
    ".raw_redis": command_raw_redis,
    ".help": command_help,
    ".exit": command_dummy_exit,
    ".commands": command_commands_list,
    "send": command_send,
    "get-instances": command_get_instances,
    "create-instance": command_create_instance,
    "delete-instance": command_delete_instance,
}

log_console_handler = logging.StreamHandler()
log_console_handler.setFormatter(logging.Formatter("[%(asctime)s %(levelname)s] %(message)s",datefmt="%H:%M:%S"))

logger = logging.getLogger("debugger")
logger.setLevel("INFO")
logger.addHandler(log_console_handler)

logger.info("Loggers initialized")

redis_host = "localhost" #input("Redis host: ")
redis_port = 6379 #input("Redis port: ")
logger.info("Attempting to connect to Redis...")
redis_client = redis.StrictRedis(host=redis_host, port=str(redis_port), db=0)
redis_pubsub = redis_client.pubsub()
redis_pubsub.subscribe("tb-null") # nothing channel just to make sure that redis doesn't freak out
# ensure that redis actually connects
if not redis_client.ping():
    logger.error("Unable to ping Redis!")
    exit(1)

logger.info("Starting listener thread...")

def handle_message_tb_controller_ping(message):
    pass #print(message)

def handle_message_tb_controller_calls(message):
    if message.type == "instance-list":
        if message.sender in waiting_responses_instances_list:
            waiting_responses_instances_list.remove(message.sender)
            logger.info("Instances list for " + message.sender)
            logger.info(json.dumps(message.data,indent=4,sort_keys=True))

def handle_message_tb_service_status(message):
    pass #print(message)

def handle_message_tb_service_calls(message):
    pass #print(message)

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
        if raw_redis_enabled:
            logger.info("MSG " + "(" + message["channel"].decode("utf-8") + ") " + message_parsed.sender + " -> " + message_parsed.recipient)
            logger.info(message_parsed.type + ": " + str(message_parsed.data))
        if message["channel"] == ("tb-controller-ping").encode("utf-8"):
            handle_message_tb_controller_ping(message_parsed)
        if message["channel"] == ("tb-controller-calls").encode("utf-8"):
            handle_message_tb_controller_calls(message_parsed)
        if message["channel"] == ("tb-service-status").encode("utf-8"):
            handle_message_tb_service_status(message_parsed)
        if message["channel"] == ("tb-service-calls").encode("utf-8"):
            handle_message_tb_service_calls(message_parsed)

logger.info("Starting message handler thread")

message_handler_thread = threading.Thread(target=message_handler_target,name="Redis mesage handler",daemon=True)
message_handler_thread.do_run = True
message_handler_thread.start()

logger.info("Welcome to the TerraBungee network debugger, brave programmer!")
logger.info("Please run \".help\" for information on commands.")

command_subscribe(["*"])
run = True
while run:
    try:
        cmd_raw = input("")
        if cmd_raw.strip() == "":
            continue
        if cmd_raw == ".exit":
            run = False
            continue
        cmd = cmd_raw.split()[0]
        args = cmd_raw.split()[1:]
        cmd_function = commands.get(cmd)
        if not cmd_function:
            logger.info("Error: no such command " + cmd)
            continue
        cmd_function(args)
    except KeyboardInterrupt:
        break
logger.info("Stopping...")
message_handler_thread.do_run = False
message_handler_thread.join()
run = False
