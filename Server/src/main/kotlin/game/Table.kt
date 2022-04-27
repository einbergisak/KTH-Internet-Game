package game

/// Pantry är där ingredienserna finns att hämta, Main är där man placerar de inför "tillagning"
enum class TableType{
    Pantry, Main
}

data class Table(val type: TableType, val foodBoxes: List<FoodBox>): Bounded{
    override val bounds: Rect
        get() = Rect(TABLE_POS, Pos(TABLE_POS.x-1, TABLE_POS.y-1))
}