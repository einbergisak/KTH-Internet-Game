from dataclasses import dataclass

from _game.content.ingredient import Ingredient


@dataclass
class Recipe:
    name: str
    ingredients: [Ingredient]

    def value(self):
        return len(self.ingredients)