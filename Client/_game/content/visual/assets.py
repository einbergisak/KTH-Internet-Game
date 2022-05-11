import os

import pygame as pg

from _game.content.ingredient import Ingredient

"""
    This file assets and constants concerning game graphics.
"""

TITLE_IMAGE = pg.image.load(os.path.join('resources', 'title.png'))

GAME_BACKGROUND_IMAGE = pg.image.load(os.path.join('resources', 'background.png'))
HEADER_BACKGROUND_COLOR = (240, 255, 240)

PLAYER1_IMAGE = pg.image.load(os.path.join('resources', 'player1.png'))
PLAYER2_IMAGE = pg.image.load(os.path.join('resources', 'player2.png'))
PLAYER_IMAGES = [PLAYER1_IMAGE, PLAYER2_IMAGE]


FOODBOX_IMAGE = pg.image.load(os.path.join('resources', 'foodbox.png'))

INGREDIENT_IMAGES = {}
for ing in Ingredient:
    INGREDIENT_IMAGES[ing] = pg.image.load(os.path.join('resources\\ingredients', f"{ing.value}.png"))

INGREDIENT_SIZE = 50