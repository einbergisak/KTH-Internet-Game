import json
import socket

from command import ReceiveCommand, SendCommand
from packet import Packet

SERVER_IP = "192.168.56.1"
SERVER_PORT = 25565
BUFFER_SIZE = 512
SERVER = (SERVER_IP, SERVER_PORT)

sckt = socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM)
# sckt.bind((SERVER_IP, SERVER_PORT))

jc = json.encoder.JSONEncoder()

print(jc.encode(Packet("CMD", "Data").json()))


def as_json_string(name: str, val: str):
    packet = {
        name: val
        }
    return jc.encode(packet)


def send(packet: Packet):
    sckt.sendto(str.encode(jc.encode(packet)), SERVER)


def read() -> dict:
    try:
        return json.loads(sckt.recv(512))
    finally:
        return {}


def connect() -> bool:
    packet = Packet(SendCommand.CONNECTION_REQUEST, as_json_string("name", "Test Client"))

    # Send connection request
    send(packet)

    # Read feedback
    recv = read()

    cmd = recv["command"]

    if cmd == ReceiveCommand.CONNECTION_ACCEPTED:
        return True
    else:
        return False

