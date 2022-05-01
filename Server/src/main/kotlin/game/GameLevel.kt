package game

import kotlinx.serialization.Serializable

@Serializable
data class GameLevel (val gameBounds: Rect, val tables: Tables){
    companion object{ fun default(): GameLevel = GameLevel(GAME_BOUNDS, Tables.default()) }
}