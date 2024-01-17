package com.kidrich.turbotrack

import java.util.Date

sealed interface MealEvent {
    object SaveMeal: MealEvent
    data class SetTimestamp(val timestamp: Date): MealEvent
    data class SetCalories(val calories: Int): MealEvent
    data class SetIsSnack(val isSnack: Boolean): MealEvent
    object ShowDialog: MealEvent
    object HideDialog: MealEvent
    data class SortType(val sortType: com.kidrich.turbotrack.SortType): MealEvent
    data class DateType(val dateType: com.kidrich.turbotrack.DateType): MealEvent
    data class DeleteMeal(val meal: Meal): MealEvent
}