package game

import kotlinx.serialization.Serializable

/**
 * 2D position, in-game coordinates.
 */
@Serializable
data class Pos(var x: Int, var y: Int)