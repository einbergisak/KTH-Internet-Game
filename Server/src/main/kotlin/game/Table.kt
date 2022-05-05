package game

import kotlinx.serialization.Serializable

/**
 * Contains information regarding the table's bounds and [FoodBox]es.
 */
@Serializable
data class Table(override val bounds: Rect, var foodBoxes: List<FoodBox>) : Bounded {

    constructor(bounds: Rect, isMain: Boolean) : this(bounds, getPositionedFoodBoxes(bounds, isMain))

    /**
     * Clears all [Ingredient]s from this [Table].
     */
    fun clearIngredients() {
        foodBoxes.forEach { it.containedIngredient = null }
    }

    companion object {
        /**
         * Returns a list of empty [FoodBox]es, positioned within supplied [bounds].
         * @param isMain Should be set to true if _this_ is the main [Table].
         */
        fun getPositionedFoodBoxes(bounds: Rect, isMain: Boolean = false): List<FoodBox> {
            val height = if (isMain) MAIN_TABLE_HEIGHT else SIDE_TABLE_HEIGHT
            val gap = height - FOODBOX_SIZE
            val spacing = gap / FOODBOXES_PER_TABLE
            return if (isMain) {
                List(FOODBOXES_PER_TABLE) { index ->
                    FoodBox(Pos(bounds.topleft.x, bounds.topleft.y + FOODBOX_SIZE * index + spacing), null)
                } + List(FOODBOXES_PER_TABLE) { index ->
                    FoodBox(
                        Pos(bounds.botright.x - FOODBOX_SIZE, bounds.topleft.y + FOODBOX_SIZE * index + spacing),
                        null
                    )
                }
            } else {
                List(FOODBOXES_PER_TABLE) { index ->
                    FoodBox(Pos(bounds.topleft.x, bounds.topleft.y + FOODBOX_SIZE * index + spacing), null)
                }
            }
        }
    }
}
