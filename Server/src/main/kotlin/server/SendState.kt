package server

import game.GameLevel
import game.GameState
import game.Player
import game.Recipe
import kotlinx.serialization.Serializable

typealias Seconds = Long

// State som skickas varje tick. Innehåller endast den relevanta datan från GameState
/**
 * State to be sent by the [Server] to both players each game tick.
 * Contains the data from the [GameState] that is relevant to the players.
 */
@Serializable
data class SendState(
    val players: Pair<Player, Player>,
    val currentRecipes: Pair<Recipe, Recipe>,
    val gameLevel: GameLevel,
    val pointsEarned: Int,
    val timeRemaining: Seconds
)