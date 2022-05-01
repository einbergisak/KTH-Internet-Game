package server

const val SECONDS_UNTIL_TIMED_OUT: Long = 5
const val SECONDS_UNTIL_DISCONNECTED: Long = 15
const val SERVER_IP = "127.0.0.1"
const val SERVER_PORT = 8888
const val TICKS_PER_SECOND = 30
const val TICK_DURATION_MILLIS = (1.0/TICKS_PER_SECOND*1000.0).toLong()