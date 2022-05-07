from dataclasses import dataclass

from player import Player
from recipe import Recipe
from table import Table, Tables


@dataclass
class GameState:
    player1: Player
    player2: Player
    recipe1: Recipe
    recipe2: Recipe
    game_bounds: ((int, int), (int, int))
    points: int
    time_remaining: int
    tables: Tables
