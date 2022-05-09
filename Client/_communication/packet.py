import json
from dataclasses import dataclass


@dataclass
class Packet:
    command: str
    data: str

    def json(self):
        return json.dumps(self, default=vars)
