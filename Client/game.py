import pygame as pg

import communication
from command import ReceiveCommand, SendCommand
from config import FRAMERATE
from game_state import GameState
from graphics import Graphics
from packet import Packet
from parsing import parse_state


def game_over():
    print("todo")


def draw():
    pg.display.flip()
    print("todo")


def handle_input():
    key = pg.key.get_pressed()


class Game:
    state: GameState
    clock = pg.time.Clock()

    def __init__(self):
        self.graphics = Graphics(self)

    def init(self, name: str) -> bool:
        while True:
            if not communication.connect(self, name):
                return False

            # Show on screen that you are connected and waiting for other player.
            self.graphics.edit_menu_text("Connected succesfully! Waiting for game start..")

            while True:
                cmd = communication.read()["command"]
                if cmd == ReceiveCommand.START_GAME:
                    communication.send(Packet(SendCommand.GAME_STARTED, ""))
                    return True
                elif cmd == ReceiveCommand.GAME_ABORTED:
                    return False

    def update(self):
        while True:
            recv = communication.read()
            cmd = recv["command"]
            data = recv["data"]
            if cmd == ReceiveCommand.UPDATE_STATE:
                handle_input()
                self.state = parse_state(data)
                draw()
            elif cmd == ReceiveCommand.GAME_OVER:
                game_over()
            elif cmd == ReceiveCommand.GAME_ABORTED:
                return
            clock.tick(FRAMERATE)
