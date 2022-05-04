package server

// Server configuration
const val SECONDS_UNTIL_TIMED_OUT: Long = 5
const val SERVER_IP = "192.168.56.1"
const val SERVER_PORT = 25565
const val TICKS_PER_SECOND = 30
const val TICK_DURATION_MILLIS = (1.0 / TICKS_PER_SECOND * 1000.0).toLong()