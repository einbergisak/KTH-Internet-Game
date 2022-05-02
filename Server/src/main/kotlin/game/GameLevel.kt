package game

import kotlinx.serialization.Serializable

@Serializable
data class GameLevel (val gameBounds: Rect = GAME_BOUNDS, val tables: Tables = Tables())