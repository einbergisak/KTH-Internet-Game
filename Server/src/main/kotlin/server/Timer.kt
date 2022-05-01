package server

import java.time.Duration
import java.time.Instant

class Timer(){
    private var mark = Instant.now()

    /**
     * Returns time elapsed in seconds
     */
    val elapsed get() = Duration.between(mark, Instant.now()).seconds

    /**
     * Resets the timer, i.e. [elapsed] == 0
     */
    fun refresh() { mark = Instant.now() }
}