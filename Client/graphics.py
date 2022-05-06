import pygame as pg
import pygame_textinput as pgt

from config import SCREEN_WIDTH, SCREEN_HEIGHT

font = pg.font.Font(None, 64)


class Graphics:
    screen = pg.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT), pg.SCALED)
    pg.display.set_caption("INET")
    pg.mouse.set_visible(False)
    _default_menu_text = font.render(f"Enter your name:", True, (40, 40, 40))
    menu_text = _default_menu_text
    textinput = pgt.TextInputVisualizer()
    menu_text_x = SCREEN_WIDTH / 2 - _default_menu_text.get_width() / 2
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
