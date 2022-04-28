package com.server

import game.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import game.Command.*

object Server {

    lateinit var connections: Connections
    lateinit var state: GameState
    

    @JvmStatic
    fun main(args: Array<String>) {
        val selectorManager = ActorSelectorManager(Dispatchers.IO)
        val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", 9002)

        val k = TestData(3, 7 to "7kobe123")

        runBlocking {

            connections = Connections.new(serverSocket)
            writeBoth("Game starting...")

            state = GameState(
                GameLevel.default(),
                Player(1, "$connections.p1.name", PLAYER1_START_POS) to Player(2, "$connections.p2.name", PLAYER2_START_POS)
            )


            // game logic

            while(true){
                Thread.sleep(2000)
                writeBoth("Skrrt")
            }

            connections.p1.socket.close()
            connections.p2.socket.close()
            connections.serverSocket.close()
        }

    }

    
    
    suspend fun writeBoth(payload: String) {
        println("writing $payload to both players")
        connections.p1.write(payload)
        connections.p2.write(payload)
    }

    fun Player.move(direction: Command, otherPlayer: Player) {

        // The other player is added to the list of collidable objects if game settings allow them to cross the table
        val objects =
            state.gameLevel.tables.getAll().toMutableList<Bounded>()
                .also { if (!MAIN_TABLE_OCCUPIES_ENTIRE_HEIGHT) it.add(otherPlayer) }

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
        for (table in state.gameLevel.tables.getAll()) {
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



