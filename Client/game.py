from time import sleep

import pygame as pg

import communication
from command import ReceiveCommand, SendCommand
from config import FRAMERATE
from game_state import GameState
from graphics import Graphics
from io_thread import IOThread
from packet import Packet


def game_over():
    print("todo")


def handle_input():
    active_keys = pg.key.get_pressed()
    cmd: SendCommand | None = None
    data: str | None = None

    # Up
    if active_keys[pg.K_w]:
        cmd = SendCommand.MOVE
        data = "UP"
    # Right
    if active_keys[pg.K_d]:
        cmd = SendCommand.MOVE
        data = "RIGHT"
    # Down
    if active_keys[pg.K_s]:
        cmd = SendCommand.MOVE
        data = "DOWN"
    # Left
    if active_keys[pg.K_a]:
        cmd = SendCommand.MOVE
        data = "LEFT"

    if cmd is not None:
        packet = Packet(cmd, data)
        communication.send(packet)


class Game:
    state: GameState | None = None
    clock = pg.time.Clock()

    def __init__(self):
        self.graphics = Graphics(self)

    def init(self, name: str) -> bool:
        while True:
            if not communication.connect(self, name):
                return False

            # Show on screen that you are connected and waiting for other player.
            self.graphics.edit_menu_text("Connected succesfully! Waiting for game start..")
            self.graphics.draw_menu()

            while True:
                pg.event.pump()
                try:
                    cmd = communication.read()["command"]
                except TimeoutError:
                    continue
                except Exception:
                    print("this shouldn't happen")
                    continue

                if cmd == ReceiveCommand.START_GAME:
                    communication.send(Packet(SendCommand.GAME_STARTED, ""))
                    return True
                elif cmd == ReceiveCommand.GAME_ABORTED:
                    return False

    def update(self):
        thread = IOThread(self)
        thread.start()
        done = False
        while not done:
            for event in pg.event.get():
                if event.type == pg.QUIT:
                    done = True
                if event.type == pg.KEYDOWN:
                    if pg.key.get_pressed()[pg.K_SPACE]:
                        communication.send(Packet(SendCommand.INTERACT_WITH_FOOD_BOX, ""))

            if not thread.is_alive():
                print("thread dead")
                break
            handle_input()
            self.graphics.draw_game()
            self.clock.tick(60)
        # Send DC TODO
        thread.done = True
