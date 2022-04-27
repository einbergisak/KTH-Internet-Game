package game

import kotlinx.serialization.Serializable

@Serializable
data class GameLevel (val gameBounds: Rect, val tables: List<Table>)