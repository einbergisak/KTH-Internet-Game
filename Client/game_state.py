from dataclasses import dataclass
from ingredient import Ingredient


@dataclass
class GameState:
    p1_pos: (int, int)
    p2_pos: (int, int)
    recipe1: (str, [str])
    recipe2: (str, [str])
    bounds: ((int, int), (int, int))
    points: int
    time_remaining: int
    left_table: ((int, int), [((int, int), Ingredient)])  # bounds, list of foodboxes (pos, content)
    right_table: ((int, int), [((int, int), Ingredient)])
    main_table: ((int, int), [((int, int), Ingredient)])

