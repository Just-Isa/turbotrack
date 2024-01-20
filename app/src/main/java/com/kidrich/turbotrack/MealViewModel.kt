package com.kidrich.turbotrack

import android.util.Log.d
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MealViewModel(
    private val mealDao: MealDao,
    private val ingredientDao: IngredientDao,
): ViewModel(){
    private val _sortType = MutableStateFlow(SortType.ALL)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _meals = _sortType
        .flatMapLatest { sortType ->
            when (sortType) {
                SortType.ALL -> mealDao.getMealsWithIngredients()
                SortType.SNACK -> mealDao.getMealsWithIngredients()
                SortType.MEAL -> mealDao.getMealsWithIngredients()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)


    private val _state = MutableStateFlow(MealState())
    val state = combine(_state, _sortType, _meals ) { state, sortType, meals  ->
        state.copy(
            meals = meals,
            sortType = sortType,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MealState());


    suspend fun onEvent(event: MealEvent) {
        when(event) {
            is MealEvent.DeleteMeal -> {
                viewModelScope.launch {
                    mealDao.deleteMeal(event.meal)
                }
            }
            MealEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingMeal = false
                ) }
            }
            MealEvent.SaveMeal -> {
                // TODO: Maybe implement later depending on need
            }
            is MealEvent.SetTimestamp-> {
                _state.update { it.copy(
                    timestamp = event.timestamp
                ) }
            }
            is MealEvent.SetIsSnack -> {
                _state.update { it.copy(
                    isSnack = event.isSnack
                ) }
            }
            MealEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingMeal = true
                ) }
            }
            is MealEvent.SortType -> {
                _sortType.value = event.sortType
            }
            is MealEvent.InsertMealWithIngredients -> {

                // UPSERT NEW MEAL
                val newMeal: Meal  = Meal(
                    isSnack = event.meal.isSnack,
                    timestamp = event.meal.timestamp,
                    mealId = event.meal.mealId
                )

                // UPSERT INGREDIENTS
                val mealId: Long = mealDao.upsertMeal(newMeal)
                d("MealViewModel", event.ingredients.toString())
                val ingredients: ArrayList<Ingredient> = arrayListOf()
                event.ingredients.forEach { ingredient: Ingredient ->
                    ingredients.add(Ingredient(
                        mealId = mealId,
                        calories = ingredient.calories,
                        name = ingredient.name
                    ))
                }
                ingredientDao.upsertIngredients(ingredients)
            }

            is MealEvent.SaveOneMeal -> {
            }
        }
    }
}