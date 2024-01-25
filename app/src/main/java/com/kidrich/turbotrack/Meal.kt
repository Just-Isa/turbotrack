package com.kidrich.turbotrack

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.google.gson.annotations.SerializedName

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
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val ingredientId: Long = 0,
    val mealId: Long = 0,

    val name: String = "",

    val calories: Int = 0,
    val grams: Int = 0,

    val alcohol: Double? = 0.0,
    @SerializedName("alcohol_100g")
    val alcohol100g: Double? = 0.0,
    @SerializedName("alcohol_serving")
    val alcoholServing: Double? = 0.0,
    @SerializedName("alcohol_unit")
    val alcoholUnit: String? = "",

    @SerializedName("alcohol_value")
    val alcoholValue: Double? = 0.0,
    val carbohydrates: Double? = 0.0,
    @SerializedName("carbohydrates_100g")
    val carbohydrates100g: Double? = 0.0,
    @SerializedName("carbohydrates_serving")
    val carbohydratesServing: Double? = 0.0,
    @SerializedName("carbohydrates_unit")
    val carbohydratesUnit: String? = "",

    @SerializedName("carbohydrates_value")
    val carbohydratesValue: Double? = 0.0,
    val energy: Double? = 0.0,
    @SerializedName("energy-kcal")
    val energyKcal: Double? = 0.0,
    @SerializedName("energy-kcal_100g")
    val energyKcal100g: Double? = 0.0,
    @SerializedName("energy-kcal_serving")
    val energyKcalServing: Double? = 0.0,
    @SerializedName("energy-kcal_unit")
    val energyKcalUnit: String? = "",

    @SerializedName("energy-kcal_value")
    val energyKcalValue: Double? = 0.0,
    @SerializedName("energy-kcal_value_computed")
    val energyKcalValueComputed: Double? = 0.0,
    val energy_100g: Double? = 0.0,
    val energy_serving: Double? = 0.0,
    @SerializedName("energy_unit")
    val energyUnit: String? = "",

    @SerializedName("energy_value")
    val energyValue: Double? = 0.0,
    val fat: Double? = 0.0,
    @SerializedName("fat_100g")
    val fat100g: Double? = 0.0,
    @SerializedName("fat_serving")
    val fatServing: Double? = 0.0,
    @SerializedName("fat_unit")
    val fatUnit: String? = "",

    @SerializedName("fat_value")
    val fatValue: Double? = 0.0,

    @SerializedName("nutrition-score-fr")
    val nutritionScoreFr: Int? = 0,
    @SerializedName("nutrition-score-fr_100g")
    val nutritionScoreFr100g: Int? = 0,

    val proteins: Double? = 0.0,
    @SerializedName("proteins_100g")
    val proteins100g: Double? = 0.0,
    @SerializedName("proteins_serving")
    val proteinsServing: Double? = 0.0,
    @SerializedName("proteins_unit")
    val proteinsUnit: String? = "",

    @SerializedName("proteins_value")
    val proteinsValue: Double? = 0.0,
    val salt: Double? = 0.0,
    @SerializedName("salt_100g")
    val salt100g: Double? = 0.0,
    @SerializedName("salt_serving")
    val saltServing: Double? = 0.0,
    @SerializedName("salt_unit")
    val saltUnit: String? = "",

    @SerializedName("salt_value")
    val saltValue: Double? = 0.0,
    val sugars: Double? = 0.0,
    @SerializedName("sugars_100g")
    val sugars100g: Double? = 0.0,
    @SerializedName("sugars_serving")
    val sugarsServing: Double? = 0.0,
    @SerializedName("sugars_unit")
    val sugarsUnit: String? = "",

    @SerializedName("sugars_value")
    val sugarsValue: Double? = 0.0
) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(ingredientId)
        parcel.writeLong(mealId)
        parcel.writeString(name)
        parcel.writeInt(calories)
        parcel.writeInt(grams)

        parcel.writeDouble(alcohol ?: 0.0)
        parcel.writeDouble(alcohol100g ?: 0.0)
        parcel.writeDouble(alcoholServing ?: 0.0)
        parcel.writeString(alcoholUnit)
        parcel.writeDouble(alcoholValue ?: 0.0)

        parcel.writeDouble(carbohydrates ?: 0.0)
        parcel.writeDouble(carbohydrates100g ?: 0.0)
        parcel.writeDouble(carbohydratesServing ?: 0.0)
        parcel.writeString(carbohydratesUnit)
        parcel.writeDouble(carbohydratesValue ?: 0.0)

        parcel.writeDouble(energy ?: 0.0)
        parcel.writeDouble(energyKcal ?: 0.0)
        parcel.writeDouble(energyKcal100g ?: 0.0)
        parcel.writeDouble(energyKcalServing ?: 0.0)
        parcel.writeString(energyKcalUnit)
        parcel.writeDouble(energyKcalValue ?: 0.0)
        parcel.writeDouble(energyKcalValueComputed ?: 0.0)
        parcel.writeDouble(energy_100g ?: 0.0)
        parcel.writeDouble(energy_serving ?: 0.0)
        parcel.writeString(energyUnit)
        parcel.writeDouble(energyValue ?: 0.0)

        parcel.writeDouble(fat ?: 0.0)
        parcel.writeDouble(fat100g ?: 0.0)
        parcel.writeDouble(fatServing ?: 0.0)
        parcel.writeString(fatUnit)
        parcel.writeDouble(fatValue ?: 0.0)

        parcel.writeInt(nutritionScoreFr ?: 0)
        parcel.writeInt(nutritionScoreFr100g ?: 0)

        parcel.writeDouble(proteins ?: 0.0)
        parcel.writeDouble(proteins100g ?: 0.0)
        parcel.writeDouble(proteinsServing ?: 0.0)
        parcel.writeString(proteinsUnit)
        parcel.writeDouble(proteinsValue ?: 0.0)

        parcel.writeDouble(salt ?: 0.0)
        parcel.writeDouble(salt100g ?: 0.0)
        parcel.writeDouble(saltServing ?: 0.0)
        parcel.writeString(saltUnit)
        parcel.writeDouble(saltValue ?: 0.0)

        parcel.writeDouble(sugars ?: 0.0)
        parcel.writeDouble(sugars100g ?: 0.0)
        parcel.writeDouble(sugarsServing ?: 0.0)
        parcel.writeString(sugarsUnit)
        parcel.writeDouble(sugarsValue ?: 0.0)
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

    constructor(parcel: Parcel) : this (
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readDouble()
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
