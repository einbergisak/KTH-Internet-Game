from config import HEADER_HEIGHT
from food_box import FoodBox
from game_state import GameState
from player import Player
from pos import Pos
from recipe import Recipe
from table import Table, Tables


def parse_tables(tables: dict) -> Tables:

    for name, t in tables.items():
        x1 = t["bounds"]["topleft"]["x"]
        x2 = t["bounds"]["botright"]["x"]
        y1 = t["bounds"]["topleft"]["y"]+HEADER_HEIGHT
        width = x2-x1
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
        pos = Pos(fb["pos"]["x"], fb["pos"]["y"])
        list.append(FoodBox(pos, fb["containedIngredient"]))
    return list


def parse_players(players: dict):
    p1 = players["first"]
    p1pos = Pos(p1["pos"]["x"], p1["pos"]["y"] + HEADER_HEIGHT)
    player1 = Player(p1pos, p1["name"])

    p2 = players["second"]
    p2pos = Pos(p2["pos"]["x"], p2["pos"]["y"] + HEADER_HEIGHT)
    player2 = Player(p2pos, p2["name"])

    return player1, player2


def parse_state(data: dict):
    player1, player2 = parse_players(data["players"])
    r1 = data["currentRecipes"]["first"]
    r2 = data["currentRecipes"]["second"]
    recipe1 = Recipe(r1["name"], r1["ingredients"])
    recipe2 = Recipe(r2["name"], r2["ingredients"])
    bounds = data["gameLevel"]["gameBounds"]
    game_bounds = ((bounds["topleft"]["x"], bounds["topleft"]["y"]+HEADER_HEIGHT), (bounds["botright"]["x"], bounds["botright"]["y"]+HEADER_HEIGHT))
    tables = parse_tables(data["gameLevel"]["tables"])
    points = data["pointsEarned"]
    time_remaining = data["timeRemaining"]
    return GameState(player1, player2, recipe1, recipe2, game_bounds, points, time_remaining, tables)
