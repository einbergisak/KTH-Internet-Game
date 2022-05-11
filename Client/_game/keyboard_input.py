import pygame as pg

from _communication import communication
from _communication.command import SendCommand
from _communication.packet import Packet


def handle_movement_input() -> bool:
    """
        Sends movement commands to the server corresponding to pressed keys.
    """
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

    if cmd is not None:
        packet = Packet(cmd, data)
        communication.send(packet)
        return True
    else:
        return False
