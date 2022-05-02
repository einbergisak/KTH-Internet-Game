package server

import java.time.Duration
import java.time.Instant

class Timer(){
    private var mark = Instant.now()

    /**
     * Returns duration corresponding to elapsed time since last timer refresh
     */
    private val elapsed get() = Duration.between(mark, Instant.now())

    /**
     * Returns time elapsed since last timer refresh in seconds
     */
    val elapsedSeconds get() = elapsed.toSeconds()

    /**
     * Returns time elapsed since last timer refresh in milliseconds
     */
    val elapsedMillis get() = elapsed.toMillis()

    /**
     * Resets the timer, i.e. [elapsedSeconds] == 0
     */
    fun refresh() { mark = Instant.now() }
}