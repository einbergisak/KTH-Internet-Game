package game

import kotlinx.serialization.Serializable
import java.lang.IllegalArgumentException


/**
 *  An in game player, separate from the [server.Connection] to
 */
@Serializable
data class Player(
    val id: Int,
    val name: Name,
    var pos: Pos = if (id == 1) PLAYER1_START_POS else if (id == 2) PLAYER2_START_POS else throw IllegalArgumentException(
        "Player ID can only be 1 or 2"
    ),
    var carriedIngredient: Ingredient? = null
) : Bounded {

    override val bounds: Rect
        get() = Rect(pos, Pos(pos.x + PLAYER_SIZE - 1, pos.y + PLAYER_SIZE - 1))

    @Serializable
    data class Name(val name: String)
}
