package com.server

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


object Server {



    @JvmStatic
    fun main(args: Array<String>) {

        runBlocking {
            val p1: Socket
            val p2: Socket
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


