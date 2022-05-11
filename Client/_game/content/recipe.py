from dataclasses import dataclass

from _game.content.ingredient import Ingredient


@dataclass
class Recipe:
    name: str
    ingredients: [Ingredient]

    def value(self):
        """
            Returns the score obtained by completing this recipe.
        """
        return len(self.ingredients)
