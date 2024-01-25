package com.kidrich.turbotrack

import android.os.Parcel
import android.os.Parcelable
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
    val calories: Int = 0,
    val grams: Int = 0
) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(ingredientId)
        parcel.writeLong(mealId)
        parcel.writeString(name)
        parcel.writeInt(calories)
        parcel.writeInt(grams)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ingredient> {
        override fun createFromParcel(parcel: Parcel): Ingredient {
            return Ingredient(parcel)
        }

        override fun newArray(size: Int): Array<Ingredient?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt()
    )
}

data class MealWithIngredients(
    @Embedded val meal: Meal,
    @Relation(
        parentColumn = "mealId",
        entityColumn = "mealId",
    )
    val ingredients: List<Ingredient>
)
