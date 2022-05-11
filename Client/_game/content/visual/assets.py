import os

import pygame as pg

from _game.config import TABLE_WIDTH, GAME_HEIGHT, MAIN_TABLE_HEIGHT
from _game.content.ingredient import Ingredient

TITLE_IMAGE = pg.image.load(os.path.join('resources', 'title.png'))

GAME_BACKGROUND_IMAGE = pg.image.load(os.path.join('resources', 'background.png'))

PLAYER1_IMAGE = pg.image.load(os.path.join('resources', 'player1.png'))
PLAYER2_IMAGE = pg.image.load(os.path.join('resources', 'player2.png'))
PLAYER_IMAGES = [PLAYER1_IMAGE, PLAYER2_IMAGE]

FOODBOX_IMAGE = pg.image.load(os.path.join('resources', 'foodbox.png'))

SIDE_TABLE_IMAGE = pg.Surface((TABLE_WIDTH, GAME_HEIGHT))
SIDE_TABLE_IMAGE.fill((120, 100, 0))
MAIN_TABLE_IMAGE = pg.Surface((TABLE_WIDTH*2, MAIN_TABLE_HEIGHT))
MAIN_TABLE_IMAGE.fill((120, 100, 0))

INGREDIENT_SIZE = 50

INGREDIENT_IMAGES = {}
for ing in Ingredient:
    INGREDIENT_IMAGES[ing] = pg.image.load(os.path.join('resources\ingredients', f"{ing.value}.png"))
