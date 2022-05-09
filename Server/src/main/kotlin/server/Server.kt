package server

import game.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Thread.sleep
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Main server class.
 */
object Server {

    lateinit var connections: Connections
    lateinit var gameState: GameState
    lateinit var socket: DatagramSocket

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking(Dispatchers.IO) {
            println(InetAddress.getLocalHost())
            println(InetAddress.getByName(SERVER_IP))
            socket = DatagramSocket(SERVER_PORT, InetAddress.getByName(SERVER_IP))
            while (true) {
                init()

                startGame()

                gameState.gameStartTime = Timer()

                // Listen to player commands concurrently while game is running
                launch { listenToPlayerCommands() }

                while (gameState.status == Status.IN_GAME) {
                    updateGame()
                    sleep(TICK_DURATION_MILLIS)
                }
                if (gameState.status == Status.ABORTED) {
                    abort()
                } else if (gameState.status == Status.GAME_OVER) {
                    gameOver()
                }
                gameState.status = Status.PRE_GAME
            }
        }
    }


    /**
     *  Attempts to start the game.
     *  Aborts if either player does not reply with handshake packet containing [ReceiveCommand.GAME_STARTED] for [SECONDS_UNTIL_TIMED_OUT] seconds
     */
    private fun startGame() {
        sendBothPlayers(SendCommand.START_GAME)
        var p1Handshake = false
        var p2Handshake = false
        while (!(p1Handshake && p2Handshake)) {
            sleep(TICK_DURATION_MILLIS)

            // Abort game if either player has timed out
            if (connections.getTimedOut() != null) {
                gameState.status = Status.ABORTED
                return
            }

            val (packet, addr) = read().extractWithAddress() ?: continue
            if (packet.command == ReceiveCommand.GAME_STARTED) {
                when (addr) {
                    connections.player1?.address -> p1Handshake = true

                    connections.player2?.address -> p2Handshake = true

                    else -> continue
                }
            } else continue
        }
        println("Both players are connected. Starting game.")
        gameState.status = Status.IN_GAME
    }

    /**
     * Updates the game, and sends the updated [GameState] to each player.
     */
    private fun updateGame() {

        // End game when timeRemaining has reached 0 seconds
        if (gameState.timeRemaining == 0L) {
            sendBothPlayers(SendCommand.GAME_OVER, "${gameState.pointsEarned}")
            gameState.status = Status.GAME_OVER
            return
        }

        if (connections.getTimedOut() != null){
            gameState.status = Status.ABORTED
            return
        }

        connections.handleInput()
        checkRecipeCompleted()
        sendUpdatedState()
    }

    /**
     *  Establishes [Connections] to players, and resets the [GameState].
     */
    private fun init() {
        connections = Connections()
        val players = connectPlayers()
        gameState = GameState(
            GameLevel(), players
        )
        gameState.showNextRecipe()
    }

    /**
     *  Tells both players that the game has been aborted.
     */
    private fun abort() {
        sendBothPlayers(SendCommand.GAME_ABORTED)
    }

    /**
     *  Tells both players that the game has ended, and sends them the final score.
     */
    private fun gameOver() {
        val points = fmt.encodeToString(gameState.pointsEarned)
        sendBothPlayers(SendCommand.GAME_OVER, points)
    }

}



