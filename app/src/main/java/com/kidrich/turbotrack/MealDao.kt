package com.kidrich.turbotrack

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Upsert
    suspend fun upsertMeal(meal: Meal)

    @Delete
    suspend fun deleteMeal(meal: Meal)

    @Query("SELECT * FROM meal ORDER BY timestamp ASC")
    fun getAllMeals(): Flow<List<Meal>>;

    @Query("SELECT * FROM meal WHERE isSnack != 1 ORDER BY timestamp ASC")
    fun getSpecificMeals(): Flow<List<Meal>>;

    @Query("SELECT * FROM meal WHERE  isSnack = 1 ORDER BY timestamp ASC")
    fun getSpecificSnacks(): Flow<List<Meal>>;

}