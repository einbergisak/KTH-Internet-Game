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
            (if (this === Server.connections.player1?.player) Server.connections.player2?.player else Server.connections.player1?.player) ?: return

        // The other player is added to the list of collidable objects if game settings allow them to cross the table
        val objects = Server.gameState.gameLevel.tables.getAll().toMutableList<Bounded>().also { it.add(otherPlayer) }

        // Movement + Collision detection: Checks for each object's bound if it overlaps the player's bound

        Outer@for (step in 0 until PLAYER_VEL) {
            val before = pos.copy()
            when (direction) {
                Direction.UP -> {
                    pos.y -= 1
                }
                Direction.LEFT -> {
                    pos.x -= 1
                }
                Direction.DOWN -> {
                    pos.y += 1
                }
                Direction.RIGHT -> {
                    pos.x += 1
                }
            }
            for (obj in objects) {
                if (this.bounds.overlaps(obj.bounds)) {
                    pos.x = before.x
                    pos.y = before.y
                }
            }
            // Coerce the player to be inside the game bounds
            pos.y = pos.y.coerceIn(MIN_Y, GAME_HEIGHT - PLAYER_SIZE)
        }

//        Coercing x-position should be redundant due to tables blocking each side of the game
//        pos.x = pos.x.coerceIn(MIN_X, MAX_X - PLAYER_SIZE)
    }
}