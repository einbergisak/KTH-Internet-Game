package server


import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.util.*

object TestClient {
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {


        }
    }

    private fun InputStream.lines() = Scanner(this).lines()

    private fun Scanner.lines() = sequence {
        while (hasNext()) {
            yield(readLine())
        }
    }


}

