from time import sleep

import pygame as pg

from _communication import communication
from _communication.command import ReceiveCommand, SendCommand
from _communication.io_thread import IOThread
from _communication.packet import Packet
from _game.config import FRAMERATE
from _game.content.visual.graphics import Graphics
from _game.game_state import GameState
from _game.keyboard_input import handle_movement_input


class Game:
    state: GameState | None = None
    clock = pg.time.Clock()
    other_disconnected = False
    game_is_over = False

    def __init__(self):
        self.graphics = Graphics(self)

    def init(self, name: str) -> bool:
        """
            Waits for the game to be started by the server.
        """
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
                else:
                    return False

    def game_over(self):
        self.game_is_over = True
        self.graphics.show_game_over_screen()

    def game_disconnect(self):
        self.other_disconnected = True
        self.graphics.show_disconnected_screen()

    def update(self):
        """
            Updates the game every tick, rendering graphics and sending data to the server.
        """
        thread = IOThread(self)
        thread.start()
        done = False

        # Update each tick
        while not done:
            if not thread.is_alive():
                break

            handle_movement_input()

            for event in pg.event.get():
                if event.type == pg.QUIT:
                    done = True
                if event.type == pg.KEYDOWN:
                    if event.key == pg.K_SPACE:
                        communication.send(Packet(SendCommand.INTERACT_WITH_FOOD_BOX, ""))
                    elif event.key == pg.K_ESCAPE:
                        communication.send(Packet(SendCommand.DISCONNECTED, ""))
                        done = True
                        break

            self.graphics.draw_game()
            self.clock.tick(FRAMERATE)
        if self.other_disconnected:
            self.graphics.show_disconnected_screen()
            sleep(3)
        elif self.game_is_over:
            self.graphics.show_game_over_screen()
            sleep(3)

        thread.done = True
