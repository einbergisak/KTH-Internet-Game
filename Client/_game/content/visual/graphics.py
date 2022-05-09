import pygame as pg
import pygame_textinput as pgt

import _game.content.ingredient as ingredient
from _game.content.visual.assets import SIDE_TABLE_IMAGE, MAIN_TABLE_IMAGE, PLAYER1_IMAGE, FOODBOX_IMAGE
from _game.config import SCREEN_WIDTH, SCREEN_HEIGHT, HEADER_BACKGROUND_COLOR, GAME_BACKGROUND_COLOR, HEADER_HEIGHT, \
    INGREDIENT_COLOR, RECIPE_FONT_SIZE, INGREDIENT_TEXT_SIZE, GAME_WIDTH, GAME_HEIGHT, RECIPE_COLOR, TABLE_WIDTH, \
    TIMER_FONT_SIZE, POPUP_FONT_SIZE

font = pg.font.Font(None, 64)

recipe_name_font = pg.font.Font(None, RECIPE_FONT_SIZE)
recipe_name_font.set_italic(True)
ingredient_font = pg.font.Font(None, INGREDIENT_TEXT_SIZE)
timer_font = pg.font.Font(None, TIMER_FONT_SIZE)
popup_font = pg.font.Font(None, POPUP_FONT_SIZE)


class Graphics:
    screen = pg.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT), pg.SCALED)
    pg.display.set_caption("INET")
    _default_menu_text = font.render(f"Enter your name:", True, (40, 40, 40))
    menu_text = _default_menu_text
    text_input = pgt.TextInputVisualizer(font_object=font)

    def __init__(self, game):
        self.game = game

    def edit_menu_text(self, text: str):
        self.menu_text = font.render(text, True, (40, 40, 40))

    def draw_menu(self):
        menu_text_x = SCREEN_WIDTH / 4 - self._default_menu_text.get_width() / 4
        menu_text_y = SCREEN_HEIGHT / 3
        self.screen.fill((250, 250, 250))
        self.screen.blit(self.menu_text, (menu_text_x, menu_text_y - 50.0))
        self.screen.blit(self.text_input.surface, (menu_text_x, menu_text_y))
        pg.display.update()

    def show_menu(self):
        self.menu_text = self._default_menu_text
        while True:
            events = pg.event.get()
            self.text_input.update(events)

            if pg.key.get_pressed()[pg.K_RETURN]:
                return self.text_input.value

            self.draw_menu()

    def draw_game(self):
        header = pg.Surface((SCREEN_WIDTH, HEADER_HEIGHT))
        header.fill(HEADER_BACKGROUND_COLOR)
        self.screen.fill(GAME_BACKGROUND_COLOR)
        self.screen.blit(header, (0, 0))

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
                recipe_pos = recipe_x, recipe_y = (TABLE_WIDTH + 30 + i * SCREEN_WIDTH / 2, 15)
                self.screen.blit(recipe_score, (recipe_x - 40, recipe_y))
                self.screen.blit(recipe_name, recipe_pos)
                # Recipe ingredient
                for n, ing in enumerate(recipe.ingredients):
                    self.screen.blit(ingredient_font.render(f"- {ing}", True, INGREDIENT_COLOR),
                                     (recipe_x + 10, recipe_y + (n + 1) * INGREDIENT_TEXT_SIZE / 1.2 + 5))

            # FoodBoxes & Ingredients
            for table in self.game.state.tables.get_all():
                for box in table.food_boxes:
                    ing = box.ingredient
                    self.screen.blit(FOODBOX_IMAGE, box.pos.as_tuple())
                    # If FoodBox contains an ingredient
                    if ing is not None:
                        img = ingredient.get_ingredient_image(ing)
                        x, y = box.pos.as_tuple()
                        ing_pos = x + 10, y + 10
                        self.screen.blit(img, ing_pos)

            # Timer
            seconds = self.game.state.time_remaining % 60
            minutes = int(self.game.state.time_remaining / 60)
            if seconds > 9:
                text = f"{minutes}:{seconds}"
            else:
                text = f"{minutes}:0{seconds}"
            timer_text = timer_font.render(text, True, (0, 0, 0))
            timer_pos = (SCREEN_WIDTH / 2 - TIMER_FONT_SIZE * 2, HEADER_HEIGHT - TIMER_FONT_SIZE)
            self.screen.blit(timer_text, timer_pos)

            # Score
            score_text = timer_font.render(f"Score: {self.game.state.points} points", True, (0, 0, 0))
            score_pos = (SCREEN_WIDTH / 2 - TABLE_WIDTH, HEADER_HEIGHT - TIMER_FONT_SIZE * 2)
            self.screen.blit(score_text, score_pos)

        pg.display.update()

    def _draw_popup(self):
        popup = pg.Surface((400, 100))
        popup.fill((80, 80, 80))
        pos = (SCREEN_WIDTH / 2 - popup.get_width() / 2, SCREEN_HEIGHT / 2 - popup.get_height() / 2)
        self.screen.blit(popup, pos)
        return pos

    def show_game_over_screen(self):
        x, y = self._draw_popup()
        text = popup_font.render(f"Game over! Score: {self.game.state.points}", True, (200, 200, 200))
        pos = x + 10, y + 30
        self.screen.blit(text, pos)
        pg.display.update()

    def show_disconnected_screen(self):
        x, y = self._draw_popup()
        text = popup_font.render(f"Player disconnected, game aborted.", True, (200, 200, 200))
        pos = x + 10, y + 30
        self.screen.blit(text, pos)
        pg.display.update()
