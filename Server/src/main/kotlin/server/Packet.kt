package server

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.SocketAddress

typealias Data = String

/**
 *  Representation of game data extracted from a [DatagramPacket]
 */
data class Packet<T: Command>(val type: T, val data: Data)

/**
 *  Extracts a [Packet] with [Command] and [Data] from _this_ [DatagramPacket]
 */
fun DatagramPacket.extract(): Packet<ReceiveCommand>? {
    return try {
        Json.decodeFromString<Packet<ReceiveCommand>>(this.data.decodeToString())
    } catch (e: Exception) {
        return null
    }
}

/**
 *  Extracts a [Packet] with [Command] and [Data], as well as the [SocketAddress], from _this_ [DatagramPacket] as a [Pair].
 */
fun DatagramPacket.extractWithAddress(): Pair<Packet<ReceiveCommand>, SocketAddress>?{
    return this.extract()?.to(this.socketAddress)
}