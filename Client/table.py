from dataclasses import dataclass

from food_box import FoodBox
from pos import Pos


@dataclass
class Table:
    pos: Pos
    width: int
    food_boxes: [FoodBox]


@dataclass
class Tables:
    left: Table
    right: Table
    main: Table

    def get_all(self) -> [Table]:
        return [self.left, self.right, self.main]
