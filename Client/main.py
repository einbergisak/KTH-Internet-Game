import pygame as pg
import communication

pg.init()
screen = pg.display.set_mode((1000, 800), pg.SCALED)
pg.display.set_caption("INET")
pg.mouse.set_visible(False)

background = pg.Surface(screen.get_size())
background = background.convert()
background.fill((250,250,250))

if __name__ == '__main__':
    communication.connect()
