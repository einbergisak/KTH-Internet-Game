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

/**
 * Holds the three [Table]s relevant to the game.
 */
@Serializable
data class Tables(
    val left: Table = Table(
        Rect(
            LEFT_TABLE_POS,
            Pos(LEFT_TABLE_POS.x + SIDE_TABLE_WIDTH, LEFT_TABLE_POS.y + SIDE_TABLE_HEIGHT)
        ), false
    ),
    val right: Table = Table(
        Rect(
            RIGHT_TABLE_POS,
            Pos(RIGHT_TABLE_POS.x + SIDE_TABLE_WIDTH, RIGHT_TABLE_POS.y + SIDE_TABLE_HEIGHT)
        ), false
    ),
    val main: Table = Table(
        Rect(
            MAIN_TABLE_POS,
            Pos(MAIN_TABLE_POS.x + MAIN_TABLE_WIDTH, MAIN_TABLE_POS.y + MAIN_TABLE_HEIGHT)
        ), true
    )
) {
    /**
     * Clears all [Ingredient]s from all [Table]s.
     */
    fun clearAll() {
        left.clearIngredients()
        right.clearIngredients()
        main.clearIngredients()
    }

    /**
     * Returns a [List] of all [Table]s.
     */
    fun getAll(): List<Table> = listOf(left, right, main)

}
