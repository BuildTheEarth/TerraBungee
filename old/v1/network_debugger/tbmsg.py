# TerraBungee
# (C) 2020 Saghetti
# tbmsg.py: Library for serializing and deserializing TerraBungee messages

import json

class Message:
    def __init__(self,sender,recipient,_type,data):
        self.sender = sender
        self.recipient = recipient
        self.type = _type
        self.data = data

def create_message(_from,to,_type,data):
    return json.dumps({
        "sender": _from,
        "recipient": to,
        "type": _type,
        "data": data,
    })

def parse_message(msg):
    parsed_msg = json.loads(msg)
    return Message(parsed_msg["sender"],parsed_msg["recipient"],parsed_msg["type"],parsed_msg["data"])
