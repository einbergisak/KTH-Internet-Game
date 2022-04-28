package game

import kotlinx.serialization.Serializable


@Serializable
data class Table(val foodBoxes: List<FoodBox> = emptyList(), override val bounds: Rect) : Bounded {
    fun clearIngredients(){
        foodBoxes.forEach { it.containedIngredient = null }
    }
}

@Serializable
data class Tables(val left: Table, val right: Table, val main: Table) {
    fun clearAll() {
        left.clearIngredients()
        right.clearIngredients()
        main.clearIngredients()
    }

    fun getAll(): List<Table> = listOf(left, right, main)

    companion object{ fun default(): Tables{
        val main = Table(bounds=Rect(MAIN_TABLE_POS, Pos(MAIN_TABLE_POS.x+MAIN_TABLE_WIDTH, MAIN_TABLE_POS.y+MAIN_TABLE_HEIGHT)))
        val left = Table(bounds=Rect(LEFT_TABLE_POS, Pos(LEFT_TABLE_POS.x+SIDE_TABLE_WIDTH, LEFT_TABLE_POS.y+SIDE_TABLE_HEIGHT)))
        val right = Table(bounds=Rect(RIGHT_TABLE_POS, Pos(RIGHT_TABLE_POS.x+SIDE_TABLE_WIDTH, RIGHT_TABLE_POS.y+ SIDE_TABLE_HEIGHT)))
        return Tables(left, right, main)
    }}
}
