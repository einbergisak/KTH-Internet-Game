package game

import kotlinx.serialization.Serializable


@Serializable
data class Table(override val bounds: Rect, var foodBoxes: List<FoodBox>) : Bounded {

    constructor(bounds: Rect, isMain: Boolean) : this(bounds, getPositionedFoodBoxes(bounds, isMain))

    fun clearIngredients() {
        foodBoxes.forEach { it.containedIngredient = null }
    }

    companion object {
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
    fun clearAll() {
        left.clearIngredients()
        right.clearIngredients()
        main.clearIngredients()
    }

    fun getAll(): List<Table> = listOf(left, right, main)

}
