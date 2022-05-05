import communication
from command import ReceiveCommand, SendCommand
from game_state import GameState
from packet import Packet
from parsing import parse_state


def init() -> bool:
    while True:
        if not communication.connect():
            continue

        # Show on screen that you are connected and waiting for other player.

        while True:
            cmd = communication.read()["command"]
            if cmd == ReceiveCommand.START_GAME:
                communication.send(Packet(SendCommand.GAME_STARTED, ""))
                return True
            elif cmd == ReceiveCommand.GAME_ABORTED:
                return False


def game_over():
    print("todo")


def draw():
    print("todo")


class Game:
    state: GameState

    def update(self):
        while True:
            recv = communication.read()
            cmd = recv["command"]
            data = recv["data"]
            if cmd == ReceiveCommand.UPDATE_STATE:
                self.state = parse_state(data)
                draw()
            elif cmd == ReceiveCommand.GAME_OVER:
                game_over()
            elif cmd == ReceiveCommand.GAME_ABORTED:
                return
