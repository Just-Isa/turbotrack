package com.kidrich.turbotrack

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity()
data class Meal (
    @PrimaryKey(autoGenerate = true)
    val mealId: Long = 0,
    val name: String,
    val timestamp: String,
    val isSnack: Boolean
)


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Meal::class,
            parentColumns = ["mealId"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Ingredient (
    @PrimaryKey(autoGenerate = true)
    val ingredientId: Long = 0,
    val mealId: Long = 0,
    val name: String = "",
    val calories: Int = 0
)

data class MealWithIngredients(
    @Embedded val meal: Meal,
    @Relation(
        parentColumn = "mealId",
        entityColumn = "mealId",
    )
    val ingredients: List<Ingredient>
)
