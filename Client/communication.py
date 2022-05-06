import json
import socket
from time import sleep

from command import ReceiveCommand, SendCommand
from packet import Packet

SERVER_IP = "192.168.56.1"
SERVER_PORT = 25565
BUFFER_SIZE = 512
SERVER = (SERVER_IP, SERVER_PORT)

sckt = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sckt.settimeout(1.0)

jc = json.encoder.JSONEncoder()

print(jc.encode(Packet("CMD", "Data").json()))


def as_json_string(name: str, val: str):
    packet = {
        name: val
    }
    return jc.encode(packet)


def send(packet: Packet):
    sckt.sendto(str.encode(packet.json()), SERVER)


def read() -> dict:
    recv = sckt.recv(1024)
    return json.loads(recv)


def connect(ctx, name: str) -> bool:
    packet = Packet(SendCommand.CONNECTION_REQUEST, name)

    # Send connection request
    send(packet)
    print(sckt.getblocking())

    # Read feedback

    try:
        recv = read()

        cmd = recv["command"]
        print(f"Received{cmd}")
        if cmd == ReceiveCommand.CONNECTION_ACCEPTED:
            return True
    except socket.timeout:
        ctx.graphics.edit_menu_text("Connection timed out, try again.")
    except ConnectionResetError:
        ctx.graphics.edit_menu_text("Connection refused, try again.")
    ctx.graphics.draw_menu()
    sleep(2)
    return False

