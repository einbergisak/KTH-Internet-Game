from dataclasses import dataclass

from food_box import FoodBox
from ingredient import Ingredient
from parsing import parse_tables
from player import Player
from pos import Pos
from recipe import Recipe
from table import Table


@dataclass
class GameState:
    player1: Player
    player2: Player
    recipe1: Recipe
    recipe2: Recipe
    game_bounds: ((int, int), (int, int))
    points: int
    time_remaining: int
    tables: [Table]
