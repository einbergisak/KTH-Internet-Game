package game

data class Recipe (val ingredients: List<Ingredient>) {

    val value: Int get() = ingredients.size

}