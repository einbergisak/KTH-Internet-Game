from dataclasses import dataclass

import pygame as pg

from pos import Pos


@dataclass
class Player:
    pos: Pos
    name: str
    surface = pg.Surface((50, 50))

    surface.fill((140, 0, 0))
