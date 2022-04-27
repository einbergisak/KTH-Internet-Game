package game

import java.time.Instant
import java.util.Collections.shuffle


class GameState (val gameLevel: GameLevel, val players: Pair<Player, Player>){
    var gameStartTime: Instant = Instant.now()
    var remainingRecipes: MutableList<Recipe> = RECIPES.shuffled().toMutableList()
    var currentRecipes: Pair<Recipe, Recipe> = remainingRecipes.removeLast() to remainingRecipes.removeLast()
    var pointsEarned: Int = 0

}