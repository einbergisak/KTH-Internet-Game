from dataclasses import dataclass

from ingredient import Ingredient
from pos import Pos


@dataclass
class FoodBox:
    pos: Pos
    ingredient = Ingredient | None
