from time import sleep

import pygame as pg

from _communication import communication
from _communication.command import ReceiveCommand, SendCommand
from _game.game_state import GameState
from _game.content.visual.graphics import Graphics
from _communication.io_thread import IOThread
from _communication.packet import Packet


def handle_input():
    active_keys = pg.key.get_pressed()
    cmd: SendCommand | None = None
    data: str | None = None

    # Up
    if active_keys[pg.K_w]:
        cmd = SendCommand.MOVE
        data = "UP"
    # Right
    elif active_keys[pg.K_d]:
        cmd = SendCommand.MOVE
        data = "RIGHT"
    # Down
    elif active_keys[pg.K_s]:
        cmd = SendCommand.MOVE
        data = "DOWN"
    # Left
    elif active_keys[pg.K_a]:
        cmd = SendCommand.MOVE
        data = "LEFT"

    packet = Packet(cmd, data)
    communication.send(packet)


class Game:
    state: GameState | None = None
    clock = pg.time.Clock()
    other_disconnected = False
    game_is_over = False

    def __init__(self):
        self.graphics = Graphics(self)

    def init(self, name: str) -> bool:
        while True:
            if not communication.connect(self, name):
                return False

            # Show on screen that you are connected and waiting for other player.
            self.graphics.edit_menu_text("Connected! Waiting for game to start..")
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

    def game_over(self):
        self.game_is_over = True
        self.graphics.show_game_over_screen()

    def game_disconnect(self):
        self.other_disconnected = True
        self.graphics.show_disconnected_screen()

    def update(self):
        thread = IOThread(self)
        thread.start()
        done = False
        while not done:
            if not thread.is_alive():
                print("thread dead")
                break

            handle_input()

            self.graphics.draw_game()

            for event in pg.event.get():
                if event.type == pg.QUIT:
                    done = True
                if event.type == pg.KEYDOWN:
                    pressed = pg.key.get_pressed()
                    if pressed[pg.K_SPACE]:
                        communication.send(Packet(SendCommand.INTERACT_WITH_FOOD_BOX, ""))
                    elif pressed[pg.K_ESCAPE]:
                        communication.send(Packet(SendCommand.DISCONNECTED, ""))
                        done = True
                        break

            self.clock.tick(30)
        if self.other_disconnected:
            self.graphics.show_disconnected_screen()
            sleep(3)
        elif self.game_is_over:
            self.graphics.show_game_over_screen()
            sleep(3)

        thread.done = True
