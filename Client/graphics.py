import pygame as pg
import pygame_textinput as pgt

import ingredient
from assets import SIDE_TABLE_IMAGE, MAIN_TABLE_IMAGE, PLAYER1_IMAGE, FOODBOX_IMAGE
from config import SCREEN_WIDTH, SCREEN_HEIGHT, HEADER_BACKGROUND_COLOR, GAME_BACKGROUND_COLOR, HEADER_HEIGHT, \
    INGREDIENT_COLOR, RECIPE_TEXT_SIZE, INGREDIENT_TEXT_SIZE, GAME_WIDTH, GAME_HEIGHT, RECIPE_COLOR, TABLE_WIDTH

font = pg.font.Font(None, 64)


class Graphics:
    screen = pg.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT), pg.SCALED)
    pg.display.set_caption("INET")
    _default_menu_text = font.render(f"Enter your name:", True, (40, 40, 40))
    menu_text = _default_menu_text
    textinput = pgt.TextInputVisualizer(font_object=font)
    menu_text_x = SCREEN_WIDTH / 4 - _default_menu_text.get_width() / 4
    menu_text_y = SCREEN_HEIGHT / 3

    def __init__(self, game):
        self.game = game

    def edit_menu_text(self, text: str):
        self.menu_text = font.render(text, True, (40, 40, 40))

    def draw_menu(self):
        self.screen.fill((250, 250, 250))
        self.screen.blit(self.menu_text, (self.menu_text_x, self.menu_text_y - 50.0))
        self.screen.blit(self.textinput.surface, (self.menu_text_x, self.menu_text_y))
        pg.display.update()

    def show_menu(self):
        self.menu_text = self._default_menu_text
        while True:
            events = pg.event.get()
            self.textinput.update(events)

            if pg.key.get_pressed()[pg.K_RETURN]:
                return self.textinput.value

            self.draw_menu()

    def draw_game(self):
        header = pg.Surface((SCREEN_WIDTH, HEADER_HEIGHT))
        header.fill(HEADER_BACKGROUND_COLOR)
        self.screen.fill(GAME_BACKGROUND_COLOR)
        self.screen.blit(header, (0, 0))
        recipe_name_font = pg.font.Font(None, RECIPE_TEXT_SIZE)
        recipe_name_font.set_italic(True)
        ingredient_font = pg.font.Font(None, INGREDIENT_TEXT_SIZE)


        todoremove = pg.Surface((GAME_WIDTH, GAME_HEIGHT))
        todoremove.fill(GAME_BACKGROUND_COLOR)
        self.screen.blit(todoremove, (0, HEADER_HEIGHT))

        if self.game.state is None:
            self.screen.blit(recipe_name_font.render("Awaiting server...", True, RECIPE_COLOR), (30, 30))
        else:

            # Tables
            self.screen.blit(SIDE_TABLE_IMAGE, self.game.state.tables.left.pos.as_tuple())
            self.screen.blit(SIDE_TABLE_IMAGE, self.game.state.tables.right.pos.as_tuple())
            self.screen.blit(MAIN_TABLE_IMAGE, self.game.state.tables.main.pos.as_tuple())

            # Players
            self.screen.blit(PLAYER1_IMAGE, self.game.state.player1.pos.as_tuple())
            self.screen.blit(PLAYER1_IMAGE, self.game.state.player2.pos.as_tuple())

            # Recipes
            for i, recipe in enumerate([self.game.state.recipe1, self.game.state.recipe2]):
                recipe_name = recipe_name_font.render(recipe.name, True, RECIPE_COLOR)
                recipe_score = recipe_name_font.render(f"{recipe.value()}p", True, (255, 0, 30))
                recipe_pos = rx, ry = (TABLE_WIDTH+30+i*SCREEN_WIDTH/2, 15)
                self.screen.blit(recipe_score, (rx - 40, ry))
                self.screen.blit(recipe_name, recipe_pos)
                for n, ing in enumerate(recipe.ingredients):
                    self.screen.blit(ingredient_font.render(f"- {ing}", True, INGREDIENT_COLOR), (rx+10, ry+(n+1)*INGREDIENT_TEXT_SIZE/1.2+5))

            # FooxBoxes & Ingredients
            for table in self.game.state.tables.get_all():
                for box in table.food_boxes:
                    ing = box.ingredient
                    self.screen.blit(FOODBOX_IMAGE, box.pos.as_tuple())
                    if ing is not None:
                        img = ingredient.get_ingredient_image(ing)
                        x, y = box.pos.as_tuple()
                        ing_pos = x+10, y+10
                        self.screen.blit(img, ing_pos)

        pg.display.update()


