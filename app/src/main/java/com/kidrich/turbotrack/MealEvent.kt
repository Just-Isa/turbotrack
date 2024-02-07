package com.kidrich.turbotrack

sealed interface MealEvent {
    data class InsertMealWithIngredients(val meal: Meal, val ingredients: List<Ingredient>): MealEvent
    data class SortType(val sortType: com.kidrich.turbotrack.SortType): MealEvent
    data class DeleteMeal(val meal: Meal): MealEvent

    data class DeleteMealById(val mealId: Long): MealEvent
}

sealed interface IngredientEvent {
    object SaveIngredient: IngredientEvent

    data class SaveIngredients(val ingredients: List<Ingredient>): IngredientEvent
    data class DeleteIngredient(val ingredient: Ingredient): IngredientEvent
    data class setName(val name: String): IngredientEvent
    data class setCalories(val calories: Int): IngredientEvent
    data class setMealId(val mealId: Long): IngredientEvent
}