package server

import java.net.DatagramPacket
import java.net.SocketAddress


/**
 *  Receives and returns a [DatagramPacket]
 */
fun read(): DatagramPacket {
    val buf = ByteArray(256)
    val packet = DatagramPacket(buf, buf.size)
    Server.socket.receive(packet)
    packet.address

    // TODO: 2022-05-01 DEBUG PRINT TO REMOVE
    println("Received Packet DatagramPacket $packet")
    return packet
}

/**
 * Creates and sends a [DatagramPacket] with the given [Command] and [Data] to the player with _this_ [SocketAddress].
 */
fun SocketAddress.send(command: SendCommand, data: Data) {
    val p = Packet(command, data)
    val sendData = (p.type.toString() + p.data).encodeToByteArray()
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
    send(command, "")
}

/**
 * Sends a [Packet] as a [DatagramPacket] to both players.
 */
fun sendBothPlayers(command: SendCommand, data: Data){
    Server.connections.player1?.address?.send(command, data)
    Server.connections.player2?.address?.send(command, data)
}

/**
 * Sends a [Packet] containing only a [SendCommand] as a [DatagramPacket] to both players.
 */
fun sendBothPlayers(command: SendCommand){
    sendBothPlayers(command, "")
}