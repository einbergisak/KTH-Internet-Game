from dataclasses import dataclass

from _game.content.ingredient import Ingredient
from _game.content.pos import Pos


@dataclass
class Player:
    pos: Pos
    name: str
    orientation: str  # Is either "Left" or "Right". Used to render the player according to which direction it's moving.
    carriedIngredient: Ingredient | None
