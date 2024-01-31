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
                    name = event.meal.name,
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
                        name = ingredient.name,
                        grams = ingredient.grams,
                        alcohol = ingredient.alcohol,
                        alcohol100g = ingredient.alcohol100g,
                        alcoholServing = ingredient.alcoholServing,
                        alcoholUnit = ingredient.alcoholUnit,
                        alcoholValue = ingredient.alcoholValue,
                        carbohydrates = ingredient.carbohydrates,
                        carbohydrates100g = ingredient.carbohydrates100g,
                        carbohydratesServing = ingredient.carbohydratesServing,
                        carbohydratesUnit = ingredient.carbohydratesUnit,
                        carbohydratesValue = ingredient.carbohydratesValue,
                        energy = ingredient.energy,
                        energy_100g = ingredient.energy_100g,
                        energy_serving = ingredient.energy_serving,
                        energyKcal = ingredient.energyKcal,
                        energyKcal100g = ingredient.energyKcal100g,
                        energyKcalServing = ingredient.energyKcalServing,
                        energyKcalUnit = ingredient.energyKcalUnit,
                        energyKcalValue = ingredient.energyKcalValue,
                        energyKcalValueComputed = ingredient.energyKcalValueComputed,
                        energyUnit = ingredient.energyUnit,
                        energyValue = ingredient.energyValue,
                        fat = ingredient.fat,
                        fat100g = ingredient.fat100g,
                        fatServing = ingredient.fatServing,
                        fatUnit = ingredient.fatUnit,
                        fatValue = ingredient.fatValue,
                        proteins = ingredient.proteins,
                        nutritionScoreFr = ingredient.nutritionScoreFr,
                        nutritionScoreFr100g = ingredient.nutritionScoreFr,
                        proteins100g = ingredient.proteins100g,
                        proteinsServing = ingredient.proteinsServing,
                        proteinsUnit = ingredient.proteinsUnit,
                        proteinsValue = ingredient.proteinsValue,
                        salt = ingredient.salt,
                        salt100g = ingredient.salt,
                        saltServing = ingredient.saltServing,
                        saltUnit = ingredient.saltUnit,
                        saltValue = ingredient.saltValue,
                        sugars = ingredient.sugars,
                        sugars100g = ingredient.sugars100g,
                        sugarsServing = ingredient.sugarsServing,
                        sugarsUnit = ingredient.sugarsUnit,
                        sugarsValue = ingredient.sugarsValue
                    ))
                }
                ingredientDao.upsertIngredients(ingredients)
            }

            is MealEvent.DeleteMealById -> {
                viewModelScope.launch {
                    mealDao.deleteMealById(event.mealId)
                }
            }
        }
    }
}