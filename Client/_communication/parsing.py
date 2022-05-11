from _game.config import HEADER_HEIGHT
from _game.content.food_box import FoodBox
from _game.content.player import Player
from _game.content.pos import Pos
from _game.content.recipe import Recipe
from _game.content.table import Table, Tables
from _game.game_state import GameState

"""
    This file contains JSON parsing functions for different classes.
"""


def parse_tables(tables: dict) -> Tables:
    for name, t in tables.items():
        x1 = t["bounds"]["topleft"]["x"]
        x2 = t["bounds"]["botright"]["x"]
        y1 = t["bounds"]["topleft"]["y"] + HEADER_HEIGHT
        width = x2 - x1
        pos = Pos(x1, y1)
        food_boxes = parse_foodboxes(t["foodBoxes"])
        table = Table(pos, width, food_boxes)
        if name == "left":
            left_table = table
        elif name == "right":
            right_table = table
        elif name == "main":
            main_table = table

    return Tables(left=left_table, right=right_table, main=main_table)


def parse_foodboxes(foodboxes: [dict]):
    list: [FoodBox] = []
    for fb in foodboxes:
        pos = Pos(fb["pos"]["x"], fb["pos"]["y"] + HEADER_HEIGHT)
        list.append(FoodBox(pos, fb["containedIngredient"]))
    return list


def parse_player(player: dict, old_player: Player | None):
    player_x, player_y = player["pos"]["x"], player["pos"]["y"] + HEADER_HEIGHT
    player_pos = Pos(player_x, player_y)

    carried_ingredient = player.get("carriedIngredient")

    if old_player is None or old_player.pos.x < player_x:
        player_orientation = "Right"
    elif old_player.pos.x > player_x:
        player_orientation = "Left"
    else:
        player_orientation = old_player.orientation

    return Player(pos=player_pos, name=player["name"], orientation=player_orientation,
                  carriedIngredient=carried_ingredient)


def parse_players(players: dict, old_players):
    if len(old_players) != 0:
        player1 = parse_player(players["first"], old_players[0])
        player2 = parse_player(players["second"], old_players[1])
    else:
        player1 = parse_player(players["first"], None)
        player2 = parse_player(players["second"], None)

    return player1, player2


def parse_state(data: dict, players) -> GameState:
    player1, player2 = parse_players(data["players"], players)
    r1 = data["currentRecipes"]["first"]
    r2 = data["currentRecipes"]["second"]
    recipe1 = Recipe(r1["name"], r1["ingredients"])
    recipe2 = Recipe(r2["name"], r2["ingredients"])
    bounds = data["gameLevel"]["gameBounds"]
    game_bounds = ((bounds["topleft"]["x"], bounds["topleft"]["y"] + HEADER_HEIGHT),
                   (bounds["botright"]["x"], bounds["botright"]["y"] + HEADER_HEIGHT))
    tables = parse_tables(data["gameLevel"]["tables"])
    points = data["pointsEarned"]
    time_remaining = data["timeRemaining"]
    return GameState(player1, player2, recipe1, recipe2, game_bounds, points, time_remaining, tables)
