package server

import game.fmt
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.SocketAddress

typealias Data = String

/**
 *  Representation of game data extracted from a [DatagramPacket]
 */
@Serializable
data class Packet<T : Command>(val command: T, val data: Data)

/**
 *  Extracts a [Packet] with [Command] and [Data] from _this_ [DatagramPacket]
 */
fun DatagramPacket.extract(): Packet<ReceiveCommand>? {
    return try {
        val s = String(this.data, 0, this.length)
        println("received: $s")
        fmt.decodeFromString<Packet<ReceiveCommand>>(s).also {  println("extracted: $it") }

    } catch (e: Exception) {
        println(e)
        return null
    }
}

/**
 *  Extracts a [Packet] with [Command] and [Data], as well as the [SocketAddress], from _this_ [DatagramPacket] as a [Pair].
 */
fun DatagramPacket.extractWithAddress(): Pair<Packet<ReceiveCommand>, SocketAddress>? {
    return this.extract()?.to(this.socketAddress).also { println("extracted with address: $it") }
}