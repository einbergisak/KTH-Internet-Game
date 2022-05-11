from enum import Enum


class SendCommand(str, Enum):
    """
        Commands that are sent from this client to the server.
    """
    MOVE = "MOVE"
    INTERACT_WITH_FOOD_BOX = "INTERACT_WITH_FOOD_BOX"
    CONNECTION_REQUEST = "CONNECTION_REQUEST"
    DISCONNECTED = "DISCONNECTED"
    GAME_STARTED = "GAME_STARTED"


class ReceiveCommand(str, Enum):
    """
        Commands that are sent from the server to this client.
    """
    CONNECTION_DENIED = "CONNECTION_DENIED"
    CONNECTION_ACCEPTED = "CONNECTION_ACCEPTED"
    START_GAME = "START_GAME"
    GAME_ABORTED = "GAME_ABORTED"
    GAME_OVER = "GAME_OVER"
    UPDATE_STATE = "UPDATE_STATE"
