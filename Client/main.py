import pygame as pg

from game import Game

pg.init()
g = Game()


if __name__ == '__main__':

    while True:
        name = g.graphics.show_menu()
        if g.init(name):
            break

    # Game started, move from menu to game

    g.update()






