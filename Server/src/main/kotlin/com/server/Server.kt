package com.server

import game.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.Instant
import game.Command.*
import java.security.spec.MGF1ParameterSpec
import java.util.*

object Server {

    lateinit var p1: Socket
    lateinit var p2: Socket
    lateinit var send1: ByteWriteChannel
    lateinit var send2: ByteWriteChannel
    lateinit var recv1: ByteReadChannel
    lateinit var recv2: ByteReadChannel
    lateinit var level: GameLevel
    lateinit var state: GameState

    @JvmStatic
    fun main(args: Array<String>) {

        runBlocking {

            val selectorManager = ActorSelectorManager(Dispatchers.IO)
            val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", 9002)

            val k = TestData(3, 7 to "7kobe123")

            println("Awaiting Player 1")
            p1 = serverSocket.accept()
            send1 = p1.openWriteChannel()
            recv1 = p1.openReadChannel()
            println("Player 1 connected from ${p1.remoteAddress}")

            println("Awaiting Player 2")
            send1.writeStringUtf8("Joined game as Player 1. Awaiting Player 2.")
            p2 = serverSocket.accept()
            send2 = p2.openWriteChannel()
            recv2 = p2.openReadChannel()
            println("Player 2 connected from ${p1.remoteAddress}")
            send1.writeStringUtf8("Player 2 connected")
            send2.writeStringUtf8("Joined game as Player 2.")
            sendBoth("Game starting...")

            state = GameState(GameLevel.default(),Player(0, PLAYER1_START_POS) to Player(1, PLAYER2_START_POS))

            p1.close()
            p2.close()
        }

    }

    suspend fun sendBoth(payload: String) {
        send1.writeStringUtf8(payload)
        send2.writeStringUtf8(payload)
    }

    fun Player.move(direction: Command, otherPlayer: Player) {

        // The other player is added to the list of collidable objects if game settings allow them to cross the table
        val objects =
            level.tables.getAll().toMutableList<Bounded>().also { if (!MAIN_TABLE_OCCUPIES_ENTIRE_HEIGHT) it.add(otherPlayer) }

        // Movement + Collision detection: Checks for each object's bound if it overlaps the player's bound
        for (obj in objects) {
            val rect: Rect = obj.bounds
            when (direction) {
                Up -> {
                    pos.y -= PLAYER_VEL
                    if (rect.overlaps(bounds)) {
                        pos.y = rect.botright.y + 1
                    }
                }
                Left -> {
                    pos.x -= PLAYER_VEL
                    if (rect.overlaps(bounds)) {
                        pos.x = rect.botright.x + 1
                    }
                }
                Down -> {
                    pos.y += PLAYER_VEL
                    if (rect.overlaps(bounds)) {
                        pos.y = rect.topleft.y - 1
                    }
                }
                Right -> {
                    pos.x += PLAYER_VEL
                    if (rect.overlaps(bounds)) {
                        pos.x = rect.topleft.x - 1
                    }
                }
                else -> error("Command $direction should not call Player.move function")
            }
        }


        // Tvinga spelaren att befinna sig inom spelplanen
        pos.y = pos.y.coerceIn(MIN_Y, MAX_Y - PLAYER_SIZE)
        pos.x = pos.x.coerceIn(MIN_X, MAX_X - PLAYER_SIZE)
    }

    // Låter spelaren plocka upp eller placera ingredienser i en matlåda om den står bredvid en.
    fun Player.interactWithFoodBox() {
        // TODO: 2022-04-28 Kolla om getAll() funkar bra
        for (table in level.tables.getAll()) {
            for (box in table.foodBoxes) {
                // Om spelaren rör en FoodBox i sidleds, samt att spelarens mittpunkt korsar den i y-led
                if ((bounds.botright.x == box.bounds.topleft.x - 1 || bounds.topleft.x == box.bounds.botright.x + 1)
                    && (box.bounds.topleft.y..box.bounds.botright.y).contains(pos.y + PLAYER_SIZE.floorDiv(2))
                ) {
                    if (carriedIngredient == null) { // Ta från FoodBox
                        if (box.containedIngredient != null) { // Om det finns en ingrediens i matlådan
                            carriedIngredient = box.containedIngredient
                            box.containedIngredient = null
                        }
                    } else { // Placera i FoodBox
                        if (box.containedIngredient == null) { // Om matlådan är tom
                            box.containedIngredient = carriedIngredient
                            carriedIngredient = null
                        }
                    }
                }
            }
        }
    }


}


