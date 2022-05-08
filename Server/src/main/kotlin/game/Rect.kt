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

        // Disjunction on x-axis
        if (topleft.x > other.botright.x || other.topleft.x > botright.x) {
            return false
        }

        // Disjunction on y-axis
        if (other.topleft.y > botright.y || topleft.y  > other.botright.y ) {
            return false
        }

        // No disjunction on either axis infers that there is an overlap
        return true

    }
}