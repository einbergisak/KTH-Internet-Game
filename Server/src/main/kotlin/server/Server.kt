package server

import game.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import java.net.DatagramSocket
import java.net.InetAddress

object Server {

    var connections: Connections = Connections()
    lateinit var gameState: GameState
    lateinit var socket: DatagramSocket

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking(Dispatchers.IO) {
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
            if (abortIfTimedOut()) return
            val (packet, addr) = read().extractWithAddress() ?: continue
            if (packet.type == ReceiveCommand.GAME_STARTED) {
                when (addr) {
                    connections.player1?.address -> p1Handshake = true

                    connections.player2?.address -> p2Handshake = true

                    else -> continue
                }
            } else continue
        }
        gameState.status = Status.IN_GAME
    }

    private fun updateGame() {

        // Ends game when time has reached 0
        if (gameState.timeRemaining == 0L) {
            sendBothPlayers(SendCommand.GAME_OVER, "${gameState.pointsEarned}")
            gameState.status = Status.GAME_OVER
            return
        }

        connections.handleInput()

    }

    /**
     *  Establishes [Connections] to players, and resets the [GameState]
     */
    private fun init() {
        val players = connectPlayers()
        connections = Connections()
        gameState = GameState(
            GameLevel(), players
        )
    }

    /**
     *  Returns false if a player has not timed out, otherwise it returns true and resets the game.
     */
    private fun abortIfTimedOut(): Boolean {
        if (connections.getTimedOut() != null) {
            sendBothPlayers(SendCommand.GAME_ABORTED)
            init()
            return true
        }
        return false
    }

}



