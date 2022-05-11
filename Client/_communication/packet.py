import json
from dataclasses import dataclass


@dataclass
class Packet:
    """
        Representation of game data sent between the client and server.
    """
    command: str
    data: str

    def json(self):
        return json.dumps(self, default=vars)
