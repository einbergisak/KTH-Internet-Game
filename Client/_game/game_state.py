from dataclasses import dataclass

from _game.content.player import Player
from _game.content.recipe import Recipe
from _game.content.table import Tables


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
