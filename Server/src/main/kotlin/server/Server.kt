package server

import game.*
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import java.net.DatagramSocket

object Server {

    val connections: Connections = Connections()
    lateinit var gameState: GameState
    lateinit var socket: DatagramSocket

    @JvmStatic
    fun main(args: Array<String>) {

        runBlocking {
            socket = DatagramSocket(SERVER_PORT)
            val players = connectPlayers()

            // Start the game once both players are connected
            gameState = GameState(
                GameLevel.default(),
                players
            )

            startGame()

            while(gameState.status == Status.IN_GAME){
                updateGame()
            }
        }


    }


    /**
     *  Attempts to start the game.
     *  Aborts if either player does not reply with handshake packet containing [ReceiveCommand.GAME_STARTED] for [SECONDS_UNTIL_TIMED_OUT] seconds
     */
    private fun startGame() {
        sendBothPlayers(SendCommand.START_GAME)
        var p1Handshake = false
        var p2Handshake = false
        while (!(p1Handshake && p2Handshake)) {
            sleep(TICK_DURATION_MILLIS)
            if (connections.getTimedOut() != null) {
                sendBothPlayers(SendCommand.GAME_ABORTED)
                gameState.status = Status.GAME_ABORTED
                return
            }
            val (packet, addr) = read().extractWithAddress() ?: continue
            if (packet.type == ReceiveCommand.GAME_STARTED) {
                when (addr) {
                    connections.player1?.address -> {
                        p1Handshake = true
                        connections.player1?.timeOfLastPackage?.refresh()
                    }
                    connections.player2?.address -> {
                        p2Handshake = true
                        connections.player2?.timeOfLastPackage?.refresh()
                    }
                    else -> continue
                }
            } else continue
        }
        gameState.status = Status.IN_GAME
    }

    private fun updateGame(){

    }

    /**
     *  Moves _this_ [Player] in the given [Direction].
     */
    fun Player.move(direction: Direction) {
        val otherPlayer = if (this === gameState.players.first) gameState.players.second else gameState.players.first

        // The other player is added to the list of collidable objects if game settings allow them to cross the table
        val objects =
            gameState.gameLevel.tables.getAll().toMutableList<Bounded>()
                .also { if (!MAIN_TABLE_OCCUPIES_ENTIRE_HEIGHT) it.add(otherPlayer) }

        // Movement + Collision detection: Checks for each object's bound if it overlaps the player's bound
        for (obj in objects) {
            val rect: Rect = obj.bounds
            when (direction) {
                Direction.UP -> {
                    pos.y -= PLAYER_VEL
                    if (rect.overlaps(bounds)) {
                        pos.y = rect.botright.y + 1
                    }
                }
                Direction.LEFT -> {
                    pos.x -= PLAYER_VEL
                    if (rect.overlaps(bounds)) {
                        pos.x = rect.botright.x + 1
                    }
                }
                Direction.DOWN -> {
                    pos.y += PLAYER_VEL
                    if (rect.overlaps(bounds)) {
                        pos.y = rect.topleft.y - 1
                    }
                }
                Direction.RIGHT -> {
                    pos.x += PLAYER_VEL
                    if (rect.overlaps(bounds)) {
                        pos.x = rect.topleft.x - 1
                    }
                }
            }
        }


        // Coerce the player to be inside of the game bounds
        pos.y = pos.y.coerceIn(MIN_Y, MAX_Y - PLAYER_SIZE)
        pos.x = pos.x.coerceIn(MIN_X, MAX_X - PLAYER_SIZE)
    }

    /**
     *  If the player is standing adjacent to a [FoodBox],
     *  pick up or drop an [Ingredient] depending on occupation of [Player.carriedIngredient] and [FoodBox.containedIngredient]
     */
    fun Player.interactWithFoodBox() {
        // TODO: 2022-04-28 Kolla om getAll() funkar bra
        for (table in gameState.gameLevel.tables.getAll()) {
            for (box in table.foodBoxes) {
                // If the player is adjacent to a FoodBox in the x-direction, and its y-axis midpoint intersects the FoodBox in the x-direction
                if ((bounds.botright.x == box.bounds.topleft.x - 1 || bounds.topleft.x == box.bounds.botright.x + 1)
                    && (box.bounds.topleft.y..box.bounds.botright.y).contains(pos.y + PLAYER_SIZE.floorDiv(2))
                ) {
                    if (carriedIngredient == null) { // Take from FoodBox
                        if (box.containedIngredient != null) { // Om det finns en ingrediens i matlådan
                            carriedIngredient = box.containedIngredient
                            box.containedIngredient = null
                        }
                    } else { // Put in FoodBox
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



