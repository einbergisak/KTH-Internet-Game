package game

import kotlinx.serialization.Serializable


@Serializable
data class Rect(var topleft: Pos, var botright: Pos) {
    fun overlaps(other: Rect): Boolean {
        return !((topleft.x > other.botright.x || other.topleft.x > botright.x) // En är till vänster om den andra
                ||
                (botright.y > other.topleft.y || other.botright.y > topleft.y)) // En är övanför den andra
        // Om inget av dem gäller så överlappar de någonstans
    }
}