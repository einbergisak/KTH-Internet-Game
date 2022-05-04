from dataclasses import dataclass

from pos import Pos


@dataclass
class Player:
    pos: Pos
    name: str
