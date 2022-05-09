import threading
from asyncio import sleep

import _communication.communication as comm
from _communication.command import ReceiveCommand
from _communication.parsing import parse_state


class IOThread(threading.Thread):
    def __init__(self, game):
        threading.Thread.__init__(self)
        self.game = game
        self.done = False

    def run(self):
        while not self.done:
            try:
                recv = comm.read()
                cmd = recv["command"]
                data = recv["data"]
                if cmd == ReceiveCommand.UPDATE_STATE:
                    self.game.state = parse_state(comm.jdec.decode(data))
                elif cmd == ReceiveCommand.GAME_OVER:
                    self.game.game_over()
                    return
                elif cmd == ReceiveCommand.GAME_ABORTED:
                    self.game.game_disconnect()
                    return
            except TimeoutError:
                return
            except Exception:
                continue
