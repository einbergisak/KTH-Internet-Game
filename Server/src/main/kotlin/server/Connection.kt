package server

import game.Player
import game.Player.Name
import game.Status
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.SocketAddress

/**
 *  Class representing a connection to a player.
 */
data class Connection(var address: SocketAddress, var player: Player, var timeOfLastPackage: Timer = Timer()) {
    var lastPacket: Packet<ReceiveCommand>? = null

    /**
     * Performs actions corresponding to the most recent [Packet] received from the player.
     */
    fun handleInput() {
        // Returns if packet is invalid.
        val (cmd, data) = lastPacket ?: return
        try {
            when (cmd) {
                ReceiveCommand.MOVE -> {
                    val direction = Json.decodeFromString<Direction>(data)
                    player.move(direction)
                }
                ReceiveCommand.INTERACT_WITH_FOOD_BOX -> {
                    player.interactWithFoodBox()
                }
                ReceiveCommand.DISCONNECTED -> {
                    sendBothPlayers(SendCommand.GAME_ABORTED)
                    Server.gameState.status = Status.ABORTED
                }
                else -> {
                    // TODO: 2022-05-03 remove debug print
                    println("Invalid command: $cmd with data: $data")
                }
            }
        } catch (e: SerializationException) {
            /* no-op */
        }
        // Sets lastPacket to null, since it has been handled
        lastPacket = null
    }
}

/**
 *  Contains a nullable Connection for each player
 */
data class Connections(var player1: Connection? = null, var player2: Connection? = null) {

    /**
     *  Returns a connection that hasn't sent a valid packet for a duration longer than [SECONDS_UNTIL_TIMED_OUT].
     *  If none exist, returns null.
     */
    fun getTimedOut(): Connection? {
        val p1t = player1?.timeOfLastPackage?.elapsedSeconds
        val p2t = player2?.timeOfLastPackage?.elapsedSeconds

        if (p1t != null) {
            if (p1t > SECONDS_UNTIL_TIMED_OUT) return player1
        }

        if (p2t != null) {
            if (p2t > SECONDS_UNTIL_TIMED_OUT) return player2
        }

        return null
    }


    /**
     * Handles the most recent [Package] received from each player.
     */
    fun handleInput() {
        player1?.handleInput()
        player2?.handleInput()
    }
}


/**
 *  Establishes a new connection with a client, returning their [Player] [Name] and [SocketAddress]
 */
fun newConnection(): Pair<Name, SocketAddress> {
    while (true) {
        val datagram = read()

        // If data recieved does not conform to the protocol, try again
        val (pckt, addr) = datagram.extractWithAddress() ?: continue
        println("Extracted $pckt succesfully in newConnection()")
        if (pckt.command == ReceiveCommand.CONNECTION_REQUEST) {
            val name = try {
                Json.decodeFromString<Name>(pckt.data)
            } catch (e: SerializationException) {
                addr.send(SendCommand.CONNECTION_DENIED)
                continue
            }.also { addr.send(SendCommand.CONNECTION_ACCEPTED) }
            return name to addr
        } else {
            continue
        }
    }
}


/**
 *  Establishes new connections to two clients, and returning corresponding [Player] objects
 */
fun connectPlayers(): Pair<Player, Player> {
    val (name1, addr1) = newConnection()
    val (name2, addr2) = newConnection()

    // TODO: 2022-05-03 Kolla hur referenserna samspelar
    Server.connections.player1 = Connection(addr1, Player(1, name1))
    Server.connections.player2 = Connection(addr2, Player(2, name2))
    return Player(1, name1) to Player(2, name2)
}