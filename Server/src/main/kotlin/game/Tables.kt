package game

import kotlinx.serialization.Serializable


/**
 * Holds the three [Table]s relevant to the game.
 */
@Serializable
data class Tables(
    val left: Table = LEFT_TABLE,
    val right: Table  = RIGHT_TABLE,
    val main: Table = MAIN_TABLE
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
