from enum import Enum

from pygame.surface import Surface

from _game.content.visual.assets import Bacon_IMAGE, Egg_IMAGE, Milk_IMAGE, Flour_IMAGE, Cheese_IMAGE, Cream_IMAGE, Pasta_IMAGE, \
    BakingPowder_IMAGE, Sugar_IMAGE, Butter_IMAGE, Salt_IMAGE, Pepper_IMAGE, Onion_IMAGE, Potato_IMAGE, Basil_IMAGE, \
    Nuts_IMAGE, Oil_IMAGE, Tomato_IMAGE, Garlic_IMAGE, Parsley_IMAGE, ChiliPepper_IMAGE


class Ingredient(str, Enum):
    Bacon = "Bacon"
    Egg = "Egg"
    Milk = "Milk"
    Flour = "Flour"
    Cheese = "Cheese"
    Cream = "Cream"
    Pasta = "Pasta"
    BakingPowder = "BakingPowder"
    Sugar = "Sugar"
    Butter = "Butter"
    Salt = "Salt"
    Pepper = "Pepper"
    Onion = "Onion"
    Potato = "Potato"
    Basil = "Basil"
    Nuts = "Nuts"
    Oil = "Oil"
    Tomato = "Tomato"
    Garlic = "Garlic"
    Parsley = "Parsley"
    ChiliPepper = "ChiliPepper"


def get_ingredient_image(ingredient: Ingredient) -> Surface:
    match ingredient:
        case "Bacon":
            return Bacon_IMAGE
        case "Egg":
            return Egg_IMAGE
        case "Milk":
            return Milk_IMAGE
        case "Flour":
            return Flour_IMAGE
        case "Cheese":
            return Cheese_IMAGE
        case "Cream":
            return Cream_IMAGE
        case "Pasta":
            return Pasta_IMAGE
        case "BakingPowder":
            return BakingPowder_IMAGE
        case "Sugar":
            return Sugar_IMAGE
        case "Butter":
            return Butter_IMAGE
        case "Salt":
            return Salt_IMAGE
        case "Pepper":
            return Pepper_IMAGE
        case "Onion":
            return Onion_IMAGE
        case "Potato":
            return Potato_IMAGE
        case "Basil":
            return Basil_IMAGE
        case "Nuts":
            return Nuts_IMAGE
        case "Oil":
            return Oil_IMAGE
        case "Tomato":
            return Tomato_IMAGE
        case "Garlic":
            return Garlic_IMAGE
        case "Parsley":
            return Parsley_IMAGE
        case "ChiliPepper":
            return ChiliPepper_IMAGE
