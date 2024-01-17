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
import java.util.Date

class MealViewModel(
    private val dao: MealDao
): ViewModel(){
    private val _sortType = MutableStateFlow(SortType.ALL)
    private val _dateType = MutableStateFlow(DateType.TODAY)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _meals = _sortType
    .flatMapLatest { sortType ->
        when (sortType) {
            SortType.ALL -> dao.getAllMeals()
            SortType.SNACK -> dao.getSpecificSnacks()
            SortType.MEAL -> dao.getSpecificMeals()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())



    private val _state = MutableStateFlow(MealState())
    val state = combine(_state, _sortType, _meals) { state, sortType, meals  ->
        state.copy(
            meals = meals,
            sortType = sortType,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MealState());


    suspend fun onEvent(event: MealEvent) {
        when(event) {
            is MealEvent.DeleteMeal -> {
                viewModelScope.launch {
                    dao.deleteMeal(event.meal)
                }
            }
            MealEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingMeal = false
                ) }
            }
            MealEvent.SaveMeal -> {

                val calories = _state.value.calories
                val isSnack = _state.value.isSnack
                val timestamp = _state.value.timestamp
                d("test","war hier")
                d("test2", calories.toString());
                if(calories == 0 ) return;

                val meal = Meal(
                    calories = calories,
                    isSnack = isSnack,
                    timestamp = timestamp
                )

                dao.upsertMeal(meal)
                d("test","war hier")

                _state.update { it.copy(
                    calories = 0,
                    isSnack = false,
                    isAddingMeal = false,
                    timestamp = Date()
                ) }
            }
            is MealEvent.SetCalories -> {
                _state.update { it.copy(
                    calories = event.calories
                ) }
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
            is MealEvent.DateType -> {
                _dateType.value = event.dateType
            }
        }
    }
}