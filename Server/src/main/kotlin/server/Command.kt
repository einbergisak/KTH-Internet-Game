package server

interface Command


/**
 *  Commands sent by the client and received by the [Server]
 */
enum class ReceiveCommand: Command {
    MOVE, INTERACT_WITH_FOOD_BOX, CONNECTION_REQUEST, DISCONNECTED, STALE, GAME_STARTED
}

/**
 *  Commands sent by the [Server] to the clients
 */
enum class SendCommand: Command {
    CONNECTION_DENIED, CONNECTION_ACCEPTED, START_GAME, GAME_ABORTED, GAME_OVER, UPDATE_STATE
}

/**
 *  Corresponds to a direction that a client wants to move its corresponding [game.Player]
 */
enum class Direction {
    UP, DOWN, LEFT, RIGHT
}