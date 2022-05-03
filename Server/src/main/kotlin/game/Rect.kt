package game

import kotlinx.serialization.Serializable


/**
 * Rectangle bounded by a top-left and a bottom-right corner as [Pos]'.
 */
@Serializable
data class Rect(var topleft: Pos, var botright: Pos) {

    /**
     * Returns true if _this_ [Rect] overlaps supplied argument.
     */
    fun overlaps(other: Rect): Boolean {
        return !((topleft.x > other.botright.x || other.topleft.x > botright.x) // En är till vänster om den andra
                ||
                (botright.y > other.topleft.y || other.botright.y > topleft.y)) // En är övanför den andra
        // Om inget av dem gäller så överlappar de någonstans
    }
}