package com.server

import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import java.lang.Thread.sleep

data class Connection(val socket: Socket) {
    private val writeChannel = socket.openWriteChannel(autoFlush = true)
    private val readChannel = socket.openReadChannel()
    lateinit var name: String


    companion object {
        suspend fun new (serverSocket: ServerSocket, id: Int): Connection{
            while (true) {
                val socket = serverSocket.accept()
                try {
                    val connection = Connection(socket)
                    println("Awaiting Player $id name")
                    connection.name = connection.read()
                    connection.write("asdasdasd")
                    println("Player $id [${connection.name}] connected from ${socket.remoteAddress}")
                    return connection
                } catch (e: Throwable) {
                    println("Player $id disconnected")
                    socket.close()
                }
            }
        }
    }

    suspend fun write(payload: String) = writeChannel.writeStringUtf8("$payload\n")
    suspend fun read(): String {
        println("reading")
        val cnl = readChannel.readUTF8Line()
        println("read")
        return cnl.toString()
    }
}
data class Connections(val serverSocket: ServerSocket, val p1: Connection, val p2: Connection) {
    companion object{
        suspend fun new (serverSocket: ServerSocket) = Connections(serverSocket, Connection.new(serverSocket, 1), Connection.new(serverSocket, 2))
    }
}