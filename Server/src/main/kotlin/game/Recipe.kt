package game

import game.Ingredient.*

data class Recipe (val name: String, val ingredients: List<Ingredient>) {
    // Låter oss skapa recept genom att skriva alla ingredienser i följd vid invocation
    constructor(name: String, vararg ingredients: Ingredient) : this(name, ingredients.asList())
    val value: Int get() = ingredients.size

}

val RECIPES : List<Recipe> = listOf(
    Recipe("Pasta Carbonara", Pasta, Bacon, Egg, Cheese, Cream, Salt, Pepper),
    Recipe("Swedish Pancakes", Egg, Flour, Milk),
    Recipe("American Pancakes", Egg, Flour, Milk, BakingPowder),
    Recipe("Sponge Cake", Egg, Sugar, Flour, BakingPowder, Milk, Butter),
    Recipe("Pasta Alfredo", Pasta, Onion, Cream, Cheese, Butter, Salt, Pepper),
    Recipe("Potato Pancake", Flour, Milk, Egg, Salt, Potato, Butter),
    Recipe("French Fries", Potato, Oil,  Salt),
    Recipe("Pasta Pesto", Pasta, Basil, Cheese, Nuts, Oil, Garlic, Salt),
    Recipe("Pizza Margherita", Flour, Tomato, Cheese, Oil, BakingPowder, Basil, Salt),
    Recipe("Tomato Soup", Tomato, Onion, Oil, Cream, Salt, Parsley),
    Recipe("Pesto Rosso", Tomato, Cheese, Oil, Nuts, Garlic, Salt, Pepper),
    Recipe("Pasta Aglio e Olio", Pasta, Garlic, Oil, Parsley, ChiliPepper, Salt, Pepper),
    Recipe("Pasta Arrabbiata", Pasta, Garlic, ChiliPepper, Oil, Tomato, Parsley, Basil, Salt),
    Recipe("Bacon and Egg", Bacon, Egg, Salt),
    Recipe("Omelette", Egg, Salt, Pepper),
    Recipe("Scones", Flour, BakingPowder, Sugar, Butter, Salt),
    Recipe("Bruschetta", Flour, Tomato, Basil, Garlic, Salt, Pepper)
)