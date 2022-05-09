from dataclasses import dataclass


@dataclass
class Pos:
    x: int
    y: int

    def as_tuple(self):
        return self.x, self.y
