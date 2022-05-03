package game

import kotlinx.serialization.Serializable

/**
 * Core game object that holds [Ingredient]s.
 */
@Serializable
data class FoodBox(val pos: Pos, var containedIngredient: Ingredient?) : Bounded {
    override val bounds: Rect
        get() = Rect(pos, Pos(pos.x + FOODBOX_SIZE - 1, pos.y + FOODBOX_SIZE - 1))
}


