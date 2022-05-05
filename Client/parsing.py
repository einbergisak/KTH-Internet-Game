from food_box import FoodBox
from game_state import GameState
from player import Player
from pos import Pos
from recipe import Recipe
from table import Table


def parse_tables(tables: dict) -> [Table]:
    list: [Table] = []
    for t in tables.values():
        x1 = t["bounds"]["topleft"]["x"]
        x2 = t["bounds"]["botright"]["x"]
        y1 = t["bounds"]["topleft"]["y"]
        width = x2-x1
        pos = Pos(x1, y1)
        food_boxes = parse_foodboxes(t["foodBoxes"])
        list.append(Table(pos, width, food_boxes))
    return list


def parse_foodboxes(foodboxes: dict):
    list: [FoodBox] = []
    for fb in foodboxes.values():
        pos = Pos(fb["pos"]["x"], fb["pos"]["y"])
        list.append(FoodBox(pos, fb["containedIngredient"]))
    return list


def parse_state(data: dict):
    p1 = data["players"]["first"]
    player1 = Player(p1["pos"], p1["name"])
    p2 = data["players"]["second"]
    player2 = Player(p2["pos"], p2["name"])
    r1 = data["currentRecipes"]["first"]
    r2 = data["currentRecipes"]["second"]
    recipe1 = Recipe(r1["name"], r1["ingredients"])
    recipe2 = Recipe(r2["name"], r2["ingredients"])
    bounds = data["gameLevel"]["gameBounds"]
    game_bounds = ((bounds["topleft"]["x"], bounds["topleft"]["y"]), (bounds["botright"]["x"], bounds["botright"]["y"]))
    tables = parse_tables(data["gameLevel"]["tables"])
    points = data["pointsEarned"]
    time_remaining = data["timeRemaining"]
    return GameState(player1, player2, recipe1, recipe2, game_bounds, points, time_remaining, tables)
