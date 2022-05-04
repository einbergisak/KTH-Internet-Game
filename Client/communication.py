import socket, json

import pygame

SERVER_IP = "192.168.56.1"
SERVER_PORT = 25565
BUFFER_SIZE = 512
SERVER = (SERVER_IP, SERVER_PORT)

sckt = socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM)
# sckt.bind((SERVER_IP, SERVER_PORT))

jc = json.encoder.JSONEncoder()


def connect():
    msg = {
        "command": "CONNECTION_REQUEST",
        "data": jc.encode({"name": "Test Client"})
    }
    sckt.sendto(str.encode(jc.encode(msg)), SERVER)
    recv = sckt.recv(512)
    print(recv)
    l = json.loads(recv)
    print(l["command"])
