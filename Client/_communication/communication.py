import json
import socket
from time import sleep

from _game import config
from _communication.command import ReceiveCommand, SendCommand
from _communication.packet import Packet

SERVER_IP = "192.168.56.1"
SERVER_PORT = 25565
BUFFER_SIZE = 512
SERVER = (SERVER_IP, SERVER_PORT)

sckt = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sckt.settimeout(config.SECONDS_UNTIL_TIMEOUT)

jenc = json.encoder.JSONEncoder()
jdec = json.JSONDecoder()

print(jenc.encode(Packet("CMD", "Data").json()))


def as_json_string(name: str, val: str):
    packet = {
        name: val
    }
    return jenc.encode(packet)


def send(packet: Packet):
    sckt.sendto(str.encode(packet.json()), SERVER)


def read() -> dict:
    """
        Reads an incoming Packet, and returns a corresponding dictionary.
        Throws TimeoutError if no package is received for config.SECONDS_UNTIL_TIMEOUT seconds
    """
    recv = sckt.recv(4096)
    return json.loads(recv)


def connect(ctx, name: str) -> bool:
    packet = Packet(SendCommand.CONNECTION_REQUEST, name)

    # Send connection request
    send(packet)

    # Read feedback
    sleep(0.2)
    try:
        recv = read()

        cmd = recv["command"]
        print(f"Received: {cmd}")
        if cmd == ReceiveCommand.CONNECTION_ACCEPTED:
            return True
    except socket.timeout:
        ctx.graphics.edit_menu_text("Connection timed out, try again.")
        ctx.graphics.draw_menu()
        sleep(1)
    except ConnectionResetError:
        ctx.graphics.edit_menu_text("Connection refused, try again.")
        ctx.graphics.draw_menu()
        sleep(1)
    return False
