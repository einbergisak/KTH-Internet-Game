package game

interface Bounded {
    val bounds: Rect

    val height get() = bounds.topleft.y-bounds.botright.y
}