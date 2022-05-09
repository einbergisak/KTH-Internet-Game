package server

import game.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.InetAddress

fun main() {
//    val gs = GameState(GameLevel(), Player(1, "qwefuohhqwuf") to Player(2, "qasdqasdqw"))
//    val ss = gs.createSendState()
//
//    val b =
//        Json.encodeToString(Packet<ReceiveCommand>(ReceiveCommand.CONNECTION_REQUEST, "QWUFIHUQWFHUIIQWFHIUOQWHUOIF"))
//
//    val name = Json.encodeToString("asd")
////    println(gs.gameLevel.gameBounds)
////    println(name)
////    println(Packet(SendCommand.START_GAME ,ser))
//    val d = fmt.encodeToString(ss)
//    println(d)

    val b = List<Int>(3) {index -> index}
    b.forEach(::println)


}