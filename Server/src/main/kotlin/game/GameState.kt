package game

import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant
import java.util.Collections.shuffle


class GameState (val gameLevel: GameLevel, val players: Pair<Player, Player>){
    var gameStartTime: Instant = Instant.now()
    var remainingRecipes: MutableList<Recipe> = RECIPES.shuffled().toMutableList()
    var currentRecipes: Pair<Recipe, Recipe> = remainingRecipes.removeLast() to remainingRecipes.removeLast()
    var pointsEarned: Int = 0

    fun createSendState(): SendState{
        return SendState(players, currentRecipes, gameLevel, pointsEarned, gameStartTime)
    }

    // Replaces recipes and empties all FoodBoxes, filling them with corresponding ingredients.
    fun showNextRecipe(){
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
        for (i in 0 until (zip.size/2)){
            val (l, r) = zip[i]
            putOnLeftSide.apply {
                add(l)
                add(r)
            }
        }
        for (i in ((zip.size)/2) until zip.size){
            val (l, r) = zip[i]
            putOnRightSide.apply {
                add(l)
                add(r)
            }
        }

        // Fill remaining FoodBoxes with ingredients that are not in any of the current recipes, in shuffled order
        val shuffledIngredients = Ingredient.values().asList().minus(ing1.union(ing2)).toMutableList().also{it.shuffle()}
        val emptyBoxesLeft = FOODBOXES_PER_TABLE-zip.size/2
        val emptyBoxesRight = FOODBOXES_PER_TABLE-(zip.size+1)/2
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
