package game

import server.SendState
import server.Server
import server.Timer
import javax.tools.JavaCompiler


/**
 * Contains all information regarding the current game.
 */
class GameState(val gameLevel: GameLevel, val players: Pair<Player, Player>) {
    var gameStartTime: Timer = Timer()
    var remainingRecipes: MutableList<Recipe> = Recipe.RECIPES.shuffled().toMutableList()
    lateinit var currentRecipes: Pair<Recipe, Recipe>
    var pointsEarned: Int = 0
    var status = Status.PRE_GAME
    val timeRemaining: Long
        get() {
            val rem = GAME_DURATION - gameStartTime.elapsedSeconds
            return if (rem < 0) 0 else rem
        }

    /**
     * Creates a [SendState] based on the current [GameState].
     */
    fun createSendState(): SendState {
        return SendState(players, currentRecipes, gameLevel, pointsEarned, timeRemaining)
    }

    /**
     * Replaces [currentRecipes] and empties all [FoodBox]es on the map,
     * filling the [FoodBox]es each "side table" with [Ingredient]s corresponding to the new [Recipe]s.
     */
    fun showNextRecipe() {
        gameLevel.tables.clearAll()

        val rec1 = remainingRecipes.removeLast()
        val rec2 = remainingRecipes.removeLast()
        currentRecipes = rec1 to rec2
        val (larger, smaller) = if (rec1.ingredients.size > rec2.ingredients.size) rec1 to rec2 else rec2 to rec1

        // Remove any common ingredients by performing set difference.
        val ing1 = smaller.ingredients.shuffled().toMutableList()
        val ing2 = larger.ingredients.minus(smaller.ingredients).shuffled().toMutableList()

        val putOnLeftSide = mutableListOf<Ingredient>()
        val putOnRightSide = mutableListOf<Ingredient>()

        // Distributes the ingredients evenly on each side.
        while (ing1.isNotEmpty() || ing2.isNotEmpty()){
            ing1.removeFirstOrNull()?.let { putOnLeftSide.add(it) }
            ing1.removeFirstOrNull()?.let { putOnRightSide.add(it) }
            ing2.removeFirstOrNull()?.let { putOnLeftSide.add(it) }
            ing2.removeFirstOrNull()?.let { putOnRightSide.add(it) }
        }

        // Fill remaining FoodBoxes with ingredients that are not in any of the current recipes, in shuffled order
        val ingredientsNotInRecipes =
            Ingredient.values().asList().minus(ing1.union(ing2)).shuffled().toMutableList()
        val emptyBoxesLeft = FOODBOXES_PER_TABLE - putOnLeftSide.size
        val emptyBoxesRight = FOODBOXES_PER_TABLE - putOnRightSide.size
        repeat(emptyBoxesLeft) {
            putOnLeftSide.add(ingredientsNotInRecipes.removeFirst())
        }
        repeat(emptyBoxesRight) {
            putOnRightSide.add(ingredientsNotInRecipes.removeFirst())
        }

        // Shuffle lists again so that the required ingredients aren't always on top.
        putOnRightSide.shuffle()
        putOnLeftSide.shuffle()

        // Fill FoodBoxes
        gameLevel.tables.left.foodBoxes.forEach { it.containedIngredient = putOnLeftSide.removeFirst() }
        gameLevel.tables.right.foodBoxes.forEach { it.containedIngredient = putOnRightSide.removeFirst() }

    }
}

/**
 * If one of the current [Recipe]s has been completed (i.e. all of its [Ingredient]s can be found on [Tables.main]),
 * increment [GameState.pointsEarned] and update the current [Recipe]s.
 */
fun checkRecipeCompleted() {
    val table = Server.gameState.gameLevel.tables.main
    for (recipe in Server.gameState.currentRecipes.toList()) {
        if (table.foodBoxes.map { it.containedIngredient }.containsAll(recipe.ingredients)) {
            Server.gameState.pointsEarned += recipe.value
            Server.gameState.showNextRecipe()
        }
    }
}


/**
 * The possible states of the game.
 */
enum class Status {
    PRE_GAME, IN_GAME, GAME_OVER, ABORTED
}