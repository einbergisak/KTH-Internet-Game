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
import java.util.*


object Server {

    lateinit var p1: Socket
    lateinit var p2: Socket
    lateinit var level: GameLevel
    lateinit var state: GameState

    @JvmStatic
    fun main(args: Array<String>) {

        runBlocking {

            val selectorManager = ActorSelectorManager(Dispatchers.IO)
            val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", 9002)

            val k = TestData(3, 7 to "7kobe123")

            p1 = serverSocket.accept()
            val send = p1.openWriteChannel()

            send.writeStringUtf8(Json.encodeToString(k))
            p1.close()
        }

    }

    fun Player.move(direction: Command, otherPlayer: Player) {
        
        // Samlar den andra spelarens bounds med bounds för all terräng
        val objects = level.tables.toMutableList<Bounded>().apply{add(otherPlayer)}

        // Kollar för varje bound om det överlappar med spelarens bound. Collision detection.
        for (obj in objects){
            val rect: Rect = obj.bounds
            when (direction) {
                Up -> {
                    pos.y -= PLAYER_VEL
                    if (rect.overlaps(bounds)) {
                        pos.y = rect.botright.y+1
                    }
                }
                Left -> {
                    pos.x -= PLAYER_VEL
                    if (rect.overlaps(bounds)) {
                        pos.x = rect.botright.x+1
                    }
                }
                Down -> {
                    pos.y += PLAYER_VEL
                    if (rect.overlaps(bounds)) {
                        pos.y = rect.topleft.y-1
                    }
                }
                Right -> {
                    pos.x += PLAYER_VEL
                    if (rect.overlaps(bounds)) {
                        pos.x = rect.topleft.x-1
                    }
                }
                InteractWithFoodBox -> error("Food box interaction should not be able to call Player.move function")
            }
        }


        // Tvinga spelaren att befinna sig inom spelplanen
        pos.y = pos.y.coerceIn(MIN_Y, MAX_Y - PLAYER_SIZE)
        pos.x = pos.x.coerceIn(MIN_X, MAX_X - PLAYER_SIZE)
    }

    fun Player.interactWithFoodBox() {
        for (table in level.tables){
            for (box in table.foodBoxes){
                // Om spelaren rör en FoodBox i sidleds, samt att spelarens mittpunkt korsar den i y-led
                if ((bounds.botright.x == box.bounds.topleft.x-1 || bounds.topleft.x == box.bounds.botright.x+1)
                    && (box.bounds.topleft.y..box.bounds.botright.y).contains(pos.y+PLAYER_SIZE.floorDiv(2))){
                    if (carriedIngredient == null) { // Ta från FoodBox
                        if (box.containedIngredient != null){ // Om det finns en ingrediens i FoodBoxen
                            carriedIngredient = box.containedIngredient
                            box.containedIngredient = null
                        }
                    } else{ // Placera i FoodBox
                        if (box.containedIngredient == null){ // Om FoodBoxen är tom
                            box.containedIngredient = carriedIngredient
                            carriedIngredient = null
                        }
                    }
                }
            }
        }
    }

    data class SendState(
        val players: Pair<Player, Player>,
        val currentRecipes: Pair<Recipe, Recipe>,
        val gameLevel: GameLevel,
        val pointsEarned: Int,
        val timeRemaining: Duration
    ) {
        // Secondary constructor som låter mig skapa en instans av dataclassen med en Instant istället för Duration som sista argument
        constructor(
            players: Pair<Player, Player>,
            currentRecipes: Pair<Recipe, Recipe>,
            gameLevel: GameLevel,
            pointsEarned: Int,
            gameStartTime: Instant
        ) : this(players, currentRecipes, gameLevel, pointsEarned, Duration.between(gameStartTime, Instant.now()))
    }
}


