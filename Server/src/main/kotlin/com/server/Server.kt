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
}

data class SendState(
    val players: Pair<Player, Player>,
    val currentRecipes: Pair<Recipe, Recipe>,
    val foodBoxes: List<FoodBox>,
    val pointsEarned: Int,
    val timeRemaining: Duration
) {
    // Secondary constructor som låter mig skapa en instans av dataclassen med en Instant istället för Duration som sista argument
    constructor(
        players: Pair<Player, Player>,
        currentRecipes: Pair<Recipe, Recipe>,
        foodBoxes: List<FoodBox>,
        pointsEarned: Int,
        gameStartTime: Instant
    ) : this(players, currentRecipes, foodBoxes, pointsEarned, Duration.between(gameStartTime, Instant.now()))
}

