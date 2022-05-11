import pygame as pg

from _game.game import Game

# Initialize PyGame
pg.init()

# Initialize game
g = Game()

if __name__ == '__main__':

    while True:
        name = g.graphics.show_menu()
        if g.init(name):
            break

    # Game started, move from menu to game
    g.update()
