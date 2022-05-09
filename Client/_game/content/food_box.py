from dataclasses import dataclass

from _game.content.ingredient import Ingredient
from _game.content.pos import Pos


@dataclass
class FoodBox:
    pos: Pos
    ingredient: Ingredient | None = None
