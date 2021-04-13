# TerraBungee
# (C) 2020 Saghetti
# tbmsg.py: Library for serializing and deserializing TerraBungee messages

import json
import redis
import uuid

class IncomingMessage:
    def __init__(self,json_data):
        self.parsed_msg = json.loads(json_data)
        self.sender = self.parsed_msg["sender"]
        self.recipient = self.parsed_msg["recipient"]
        self.type = self.parsed_msg["type"]
        self.data = self.parsed_msg["data"]
        self.is_request = (self.parsed_msg.get("request-uuid") != None)
        self.is_response = (self.parsed_msg.get("response-uuid") != None)
        self.request_uuid = self.parsed_msg.get("response-uuid")

    def respond(self,pubsub_instance,channel,data):
        if not self.is_request:
            raise RuntimeError("Attempted to respond to a non-request message")
        pubsub_instance.publish(json.dumps({
            "sender": self.recipient,
            "recipient": self.sender,
            "type": self.type,
            "data": self.data,
            "response-uuid": self.request_uuid
        }))

class OutgoingBroadcastMessage:
    def __init__(self,sender,recipient,_type,data):
        self.sender = sender
        self.recipient = recipient
        self.type = _type
        self.data = data

    def send(self,pubsub_instance,channel):
        pubsub_instance.publish(json.dumps({
            "sender": self.sender,
            "recipient": self.recipient,
            "type": self.type,
            "data": self.data
        }))

class OutgoingRequestMessage:
    def __init__(self,sender,recipient,_type,data):
        self.sender = sender
        self.recipient = recipient
        self.type = _type
        self.data = data

    def send(self,pubsub_instance,channel):
        pubsub_instance.publish(json.dumps({
            "sender": self.sender,
            "recipient": self.recipient,
            "type": self.type,
            "data": self.data
        }))
