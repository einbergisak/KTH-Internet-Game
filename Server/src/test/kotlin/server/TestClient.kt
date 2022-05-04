package server

import game.GameLevel
import game.GameState
import game.Player
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.InetAddress

fun main() {
    val gs = GameState(GameLevel(), Player(1, Player.Name("qwefuohhqwuf")) to Player(2, Player.Name("qasdqasdqw")))
    val ss = gs.createSendState()
    val ser = Json.encodeToString(ss)

    val b =
        Json.encodeToString(Packet<ReceiveCommand>(ReceiveCommand.CONNECTION_REQUEST, "QWUFIHUQWFHUIIQWFHIUOQWHUOIF"))

    val name = Json.encodeToString(Player.Name("asd"))
    println(InetAddress.getLocalHost())
    println(name)
}