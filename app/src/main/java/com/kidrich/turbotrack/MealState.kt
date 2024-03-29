package com.kidrich.turbotrack

data class MealState (
    val meals: List<MealWithIngredients> = emptyList(),
    val dayId: Long = 0,
    val timestamp : String = "",
    val isSnack: Boolean = false,
    val sortType: SortType = SortType.ALL,
)

data class IngredientState(
    val ingredients: List<Ingredient> = emptyList(),
    val name: String = "",
    val mealId: Long = 0,
    val calories: Int = 0
)

