package game

import kotlinx.serialization.Serializable
import server.Direction
import server.Server


/**
 *  An in game player.
 */
@Serializable
data class Player(
    val id: Int,
    val name: String,
    var pos: Pos = if (id == 1) PLAYER1_START_POS else if (id == 2) PLAYER2_START_POS else throw IllegalArgumentException(
        "Player ID can only be 1 or 2"
    ),
    var carriedIngredient: Ingredient? = null
) : Bounded {

    override val bounds: Rect
        get() = Rect(pos, Pos(pos.x + PLAYER_SIZE - 1, pos.y + PLAYER_SIZE - 1))


    /**
     *  If the player is standing adjacent to a [FoodBox],
     *  pick up or drop an [Ingredient] depending on occupation of [Player.carriedIngredient] and [FoodBox.containedIngredient]
     */
    fun interactWithFoodBox() {
        // TODO: 2022-04-28 Kolla om getAll() funkar bra
        for (table in Server.gameState.gameLevel.tables.getAll()) {
            for (box in table.foodBoxes) {
                // If the player is adjacent to a FoodBox in the x-direction, and its y-axis midpoint intersects the FoodBox in the x-direction
                if ((bounds.botright.x == box.bounds.topleft.x - 1 || bounds.topleft.x == box.bounds.botright.x + 1) && (box.bounds.topleft.y..box.bounds.botright.y).contains(
                        pos.y + PLAYER_SIZE.floorDiv(2)
                    )
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


    /**
     *  Moves _this_ [Player] in the given [Direction].
     */
    fun move(direction: Direction) {
        val otherPlayer =
            if (this === Server.gameState.players.first) Server.gameState.players.second else Server.gameState.players.first

        // The other player is added to the list of collidable objects if game settings allow them to cross the table
        val objects = Server.gameState.gameLevel.tables.getAll().toMutableList<Bounded>()
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


        // Coerce the player to be inside the game bounds
        pos.y = pos.y.coerceIn(MIN_Y, MAX_Y - PLAYER_SIZE)
        pos.x = pos.x.coerceIn(MIN_X, MAX_X - PLAYER_SIZE)
    }
}