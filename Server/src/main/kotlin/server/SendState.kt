package server

import game.GameLevel
import game.Player
import game.Recipe
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant


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