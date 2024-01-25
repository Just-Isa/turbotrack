package com.kidrich.turbotrack

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(
    entities = [Meal::class, Ingredient::class],
    version = 14,
    exportSchema = false
)
abstract class MealDatabase: RoomDatabase() {
    abstract val mealDao: MealDao
    abstract val ingredientDao: IngredientDao

    companion object {

        @Volatile
        private var INSTANCE: MealDatabase? = null

        fun getInstance(context: Context): MealDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MealDatabase::class.java,
                        "sleep_history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}