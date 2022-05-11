from dataclasses import dataclass

import pygame as pg

from _game.content.ingredient import Ingredient
from _game.content.pos import Pos


@dataclass
class Player:
    pos: Pos
    name: str
    orientation: str
    surface = pg.Surface((50, 50))
    carriedIngredient: Ingredient | None


    surface.fill((140, 0, 0))
