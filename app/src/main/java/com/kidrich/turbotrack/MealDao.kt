package com.kidrich.turbotrack

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Upsert
    suspend fun upsertMeal(meal: Meal): Long

    @Transaction
    @Upsert
    suspend fun insertMealWithIngredients(meal: Meal, ingredients: List<Ingredient>)

    @Delete
    suspend fun deleteMeal(meal: Meal)

    @Query("DELETE FROM meal WHERE meal.mealId = :mealId")
    suspend fun deleteMealById(mealId: Long)

    @Transaction
    @Query("SELECT * FROM meal")
    fun getMealsWithIngredients(): Flow<List<MealWithIngredients>>

    @Transaction
    @Query("SELECT * FROM meal WHERE mealId = :id")
    fun getMealsWithIngredientsById(id: Long): Flow<MealWithIngredients>

    @Query("SELECT * FROM meal ORDER BY timestamp DESC")
    fun getAllMeals(): Flow<List<Meal>>;

    @Query("SELECT * FROM meal WHERE isSnack != 1 ORDER BY timestamp DESC")
    fun getSpecificMeals(): Flow<List<Meal>>;

    @Query("SELECT * FROM meal WHERE  isSnack = 1 ORDER BY timestamp DESC")
    fun getSpecificSnacks(): Flow<List<Meal>>;
}

@Dao
interface IngredientDao {
    @Upsert
    suspend fun upsertIngredient(ingredients: Ingredient)

    @Upsert
    suspend fun upsertIngredients(ingredients: List<Ingredient>)

    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    @Query("SELECT * FROM ingredient")
    fun getAllIngredients() : Flow<List<Ingredient>>

}