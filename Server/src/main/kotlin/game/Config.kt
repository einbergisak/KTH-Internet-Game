package game

import kotlinx.serialization.json.Json

// Game configuration
const val MAIN_TABLE_OCCUPIES_ENTIRE_HEIGHT = true // Implemented due to uncertainty regarding project criteria
const val GAME_WIDTH = 1200
const val GAME_HEIGHT = 600
const val PLAYER_VEL = 6
const val MIN_X = 0
const val MAX_X = GAME_WIDTH - 1
const val MIN_Y = 0
const val MAX_Y = GAME_HEIGHT - 1
const val PLAYER_SIZE = 80
const val FOODBOXES_PER_TABLE = 5
const val SIDE_TABLE_HEIGHT = GAME_HEIGHT
val MAIN_TABLE_HEIGHT =
    if (MAIN_TABLE_OCCUPIES_ENTIRE_HEIGHT) SIDE_TABLE_HEIGHT else SIDE_TABLE_HEIGHT - PLAYER_SIZE * 2
const val FOODBOX_SIZE = 100
const val SIDE_TABLE_WIDTH = FOODBOX_SIZE
const val MAIN_TABLE_WIDTH = SIDE_TABLE_WIDTH * 2
val MAIN_TABLE_POS = Pos(MAX_X / 2 - MAIN_TABLE_WIDTH / 2, 0)
val LEFT_TABLE_POS = Pos(MIN_X, MIN_Y)
val RIGHT_TABLE_POS = Pos(GAME_WIDTH - SIDE_TABLE_WIDTH, MIN_Y)
val GAME_BOUNDS = Rect(Pos(MIN_X, MIN_Y), Pos(MAX_X, MAX_Y))
val PLAYER1_START_POS = Pos(MIN_X + SIDE_TABLE_WIDTH*2, MAX_Y / 2)
val PLAYER2_START_POS = Pos(GAME_WIDTH - SIDE_TABLE_WIDTH*2 - PLAYER_SIZE, MAX_Y / 2)
const val GAME_DURATION
        /** In seconds */
        : Long = 180
val LEFT_TABLE = Table(
    Rect(
        LEFT_TABLE_POS,
        Pos(LEFT_TABLE_POS.x + SIDE_TABLE_WIDTH - 1, LEFT_TABLE_POS.y + SIDE_TABLE_HEIGHT - 1)
    ), false
)
val RIGHT_TABLE = Table(
    Rect(
        RIGHT_TABLE_POS,
        Pos(RIGHT_TABLE_POS.x + SIDE_TABLE_WIDTH - 1, RIGHT_TABLE_POS.y + SIDE_TABLE_HEIGHT - 1)
    ), false
)
val MAIN_TABLE = Table(
    Rect(
        MAIN_TABLE_POS,
        Pos(MAIN_TABLE_POS.x + MAIN_TABLE_WIDTH - 1, MAIN_TABLE_POS.y + MAIN_TABLE_HEIGHT - 1)
    ), true
)
val fmt = Json {
    encodeDefaults = true
    isLenient = true
}