package server


import game.fmt
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.SocketAddress


/**
 *  Receives and returns a [DatagramPacket]
 */
fun read(): DatagramPacket {
    val buf = ByteArray(128)
    val packet = DatagramPacket(buf, buf.size)
    Server.socket.receive(packet)

    // Refreshes timeOfLastPackage for the player who sent the packet.
    when (packet.socketAddress) {
        Server.connections.player1?.address -> {
            Server.connections.player1?.timeOfLastPackage?.refresh()
        }
        Server.connections.player2?.address -> {
            Server.connections.player2?.timeOfLastPackage?.refresh()
        }
    }

    // TODO: 2022-05-01 DEBUG PRINT TO REMOVE
    println("Received Packet DatagramPacket $packet")
    return packet
}

/**
 * Creates and sends a [DatagramPacket] with the given [Command] and [Data] to the player with _this_ [SocketAddress].
 */
fun SocketAddress.send(command: SendCommand, data: Data) {
    val p = Packet(command, data)
    val sendData = fmt.encodeToString(p).encodeToByteArray()
    val buf = ByteArray(sendData.size)
    val packet = DatagramPacket(sendData, buf.size, this)
    Server.socket.send(packet)
    // TODO: 2022-05-01 DEBUG PRINT TO REMOVE
    println("Sending Packet $p as DatagramPacket $packet")
}

/**
 * Creates and sends a [DatagramPacket] containing the [Command] to the player with _this_ [SocketAddress].
 */
fun SocketAddress.send(command: SendCommand) {
    send(command, "nil")
}

/**
 * Sends a [Packet] as a [DatagramPacket] to both players.
 */
fun sendBothPlayers(command: SendCommand, data: Data) {
    Server.connections.player1?.address?.send(command, data)
    Server.connections.player2?.address?.send(command, data)
}

/**
 * Sends a [Packet] containing only a [SendCommand] as a [DatagramPacket] to both players.
 */
fun sendBothPlayers(command: SendCommand) {
    sendBothPlayers(command, "")
}

/**
 * Sends the current state to both players as a [SendState].
 */
fun sendUpdatedState() {
    val sendState = Server.gameState.createSendState()
    val json = fmt.encodeToString(sendState)
    sendBothPlayers(SendCommand.UPDATE_STATE, json)
}

/**
 * Receives [Packet]s from players, and stores them as the most recent packet of the sending player.
 */
suspend fun listenToPlayerCommands() {
    while (Server.connections.player1 != null && Server.connections.player2 != null) {
        val (pck, addr) = read().extractWithAddress() ?: continue

        when (addr) {
            Server.connections.player1?.address -> Server.connections.player1?.lastPacket = pck
            Server.connections.player2?.address -> Server.connections.player2?.lastPacket = pck

            // Might be an incoming connection request from third party, so reply with a denial just in case
            else -> addr.send(SendCommand.CONNECTION_DENIED)
        }
    }
}