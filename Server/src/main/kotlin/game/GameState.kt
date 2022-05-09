package game

import server.SendState
import server.Server
import server.Timer


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
        val ing1 = rec1.ingredients
        val ing2 = rec2.ingredients

        val zip = ing1.zip(ing2)
        val putOnLeftSide = mutableListOf<Ingredient>()
        val putOnRightSide = mutableListOf<Ingredient>()

        // Adds half of ingredients for each recipe to each 'Side table'
        for (i in 0 until (zip.size / 2)) {
            val (l, r) = zip[i]
            putOnLeftSide.apply {
                add(l)
                add(r)
            }
        }
        for (i in ((zip.size) / 2) until zip.size) {
            val (l, r) = zip[i]
            putOnRightSide.apply {
                add(l)
                add(r)
            }
        }

        // Fill remaining FoodBoxes with ingredients that are not in any of the current recipes, in shuffled order
        val shuffledIngredients =
            Ingredient.values().asList().minus(ing1.union(ing2)).toMutableList().also { it.shuffle() }
        val emptyBoxesLeft = FOODBOXES_PER_TABLE - zip.size / 2
        val emptyBoxesRight = FOODBOXES_PER_TABLE - (zip.size + 1) / 2
        repeat(emptyBoxesLeft) {
            putOnLeftSide.add(shuffledIngredients.removeFirst())
        }
        repeat(emptyBoxesRight) {
            putOnRightSide.add(shuffledIngredients.removeFirst())
        }

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