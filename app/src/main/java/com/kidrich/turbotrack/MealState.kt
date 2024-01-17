package com.kidrich.turbotrack

import java.util.Date

data class MealState (
    val meals: List<Meal> = emptyList(),
    val dateType: DateType = DateType.TODAY,
    val timestamp : Date = Date(),
    val calories: Int = 0,
    val isSnack: Boolean = false,
    val isAddingMeal: Boolean = false,
    val sortType: SortType = SortType.ALL
)