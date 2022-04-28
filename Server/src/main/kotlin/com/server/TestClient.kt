package com.server

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.util.*

object TestClient {
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect("127.0.0.1", port = 9002)
            val read = socket.openReadChannel()
            val write = socket.openWriteChannel(autoFlush = true)

            val b = launch(Dispatchers.IO){
                while(true){
                    val line = read.readUTF8Line()
                    println("server: $line")
                }
            }

            val c = launch(Dispatchers.IO){
                for (line in System.`in`.lines()) {
                    println("client: $line")
                    write.writeStringUtf8("$line\n")
                }
            }

            println("hello")
            b.join()
            c.join()

        }
    }

    private fun InputStream.lines() = Scanner(this).lines()

    private fun Scanner.lines() = sequence {
        while (hasNext()) {
            yield(readLine())
        }
    }
}