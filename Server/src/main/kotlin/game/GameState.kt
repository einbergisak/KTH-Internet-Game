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
}

typealias Seconds = Long

// State som skickas varje tick. Innehåller endast den relevanta datan från GameState
@Serializable
data class SendState(
    val players: Pair<Player, Player>,
    val currentRecipes: Pair<Recipe, Recipe>,
    val gameLevel: GameLevel,
    val pointsEarned: Int,
    val timeRemaining: Seconds
) {
    // Secondary constructor som låter mig skapa en instans av dataclassen med en Instant istället för Duration som sista argument
    constructor(
        players: Pair<Player, Player>,
        currentRecipes: Pair<Recipe, Recipe>,
        gameLevel: GameLevel,
        pointsEarned: Int,
        gameStartTime: Instant
    ) : this(players, currentRecipes, gameLevel, pointsEarned, Duration.between(gameStartTime, Instant.now()).toSeconds())
}