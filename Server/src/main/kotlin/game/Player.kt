package game

import kotlinx.serialization.Serializable

@Serializable
data class Player(val id: Int, val name: String, var pos: Pos, var carriedIngredient: Ingredient?): Bounded {

    // If initialized without carried ingredient
    constructor(id: Int, name: String, pos: Pos): this(id, name, pos, null)

    override val bounds: Rect
        get() = Rect(pos, Pos(pos.x+PLAYER_SIZE-1, pos.y+ PLAYER_SIZE-1))
}
