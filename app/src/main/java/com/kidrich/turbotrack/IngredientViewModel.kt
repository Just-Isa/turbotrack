package com.kidrich.turbotrack

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class IngredientViewModel(
    private val ingredientDao: IngredientDao
): ViewModel(){
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _ingredients = flow {
        val ingredients = withContext(Dispatchers.IO) {
            ingredientDao.getAllIngredients()
        }
        emit(ingredients)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList<Ingredient>() )

    private val _state = MutableStateFlow(IngredientState())

    suspend fun onEvent(event: IngredientEvent) {
        when(event) {
            is IngredientEvent.DeleteIngredient -> {
                viewModelScope.launch {
                    ingredientDao.deleteIngredient(event.ingredient)
                }
            }
            IngredientEvent.SaveIngredient -> {
                    val name: String = _state.value.name
                    val calories: Int = _state.value.calories
                    val mealId: Long = _state.value.mealId

                    if (name.isBlank()) return

                    val ingredient = Ingredient(calories = calories, mealId = mealId, name= name)

                    ingredientDao.upsertIngredient(ingredient)
            }
            is IngredientEvent.setCalories -> {
                _state.update { it.copy(
                    calories = event.calories
                ) }
            }
            is IngredientEvent.setName -> {
                _state.update { it.copy(
                    name = event.name
                ) }
            }
            is IngredientEvent.setMealId -> {
                _state.update { it.copy(
                    mealId = event.mealId
                ) }
            }

            is IngredientEvent.SaveIngredients -> {
                ingredientDao.upsertIngredients(event.ingredients)
            }
        }
    }
}