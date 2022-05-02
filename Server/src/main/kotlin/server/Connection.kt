package server

import game.Player
import game.Player.Name
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.SocketAddress

/**
 *  Class representing a connection to a player
 */
data class Connection(var address: SocketAddress, var timeOfLastPackage: Timer = Timer()) {
    var lastPacket: Packet<ReceiveCommand>? = null

    fun handleInput() {
        // Returns if invalid packet
        val (cmd, data) = lastPacket ?: return
        try {
            when (cmd) {
                ReceiveCommand.MOVE -> TODO()
                ReceiveCommand.INTERACT_WITH_FOOD_BOX -> TODO()
                ReceiveCommand.CONNECTION_REQUEST -> TODO()
                ReceiveCommand.DISCONNECTED -> TODO()
                ReceiveCommand.STALE -> TODO()
                ReceiveCommand.GAME_STARTED -> TODO()
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
     *  Returns a connection that hasn't sent a valid packet for a duration longer than [s].
     *  If none exist, returns null.
     */
    private fun getTimedOutForLongerThan(s: Long): Connection? {
        val p1t = player1?.timeOfLastPackage?.elapsedSeconds
        val p2t = player2?.timeOfLastPackage?.elapsedSeconds

        if (p1t != null) {
            if (p1t > s) return player1
        }

        if (p2t != null) {
            if (p2t > s) return player2
        }

        return null
    }

    /**
     *  Returns a connection that hasn't sent a valid packet for a duration longer than [SECONDS_UNTIL_TIMED_OUT].
     *  If none exist, returns null.
     */
    fun getTimedOut(): Connection? {
        return getTimedOutForLongerThan(SECONDS_UNTIL_TIMED_OUT)
    }


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

        if (pckt.type == ReceiveCommand.CONNECTION_REQUEST) {
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

    Server.connections.player1 = Connection(addr1)
    Server.connections.player2 = Connection(addr2)
    return Player(1, name1) to Player(2, name2)
}

/**
 *  Returns a boolean telling whether _this_ [SocketAddress] is currently connected to the game
 */
fun SocketAddress.isCurrentPlayer(): Boolean {
    return (this == Server.connections.player1?.address || this == Server.connections.player2?.address)
}