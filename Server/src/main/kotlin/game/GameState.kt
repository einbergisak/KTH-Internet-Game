package game

import java.time.Instant


class GameState (val gameLevel: GameLevel, players: Pair<Player, Player>){
    var gameStartTime: Instant = Instant.now()
//    val currentRecipes: Pair<Recipe, Recipe> = {
//
//    }
//    val remainingRecipes: MutableList<Recipe> = {
//
//    }
    val pointsEarned: Int = 0

}