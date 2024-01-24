import com.google.gson.annotations.SerializedName

data class ProductResponse(
    val code: String,
    val product: Product,
    val status: Int,
    @SerializedName("status_verbose")
    val statusVerbose: String
)

data class Product(
    val nutriments: Nutriments,
    val product_name: String
)

data class Nutriments(
    val alcohol: Double,
    @SerializedName("alcohol_100g")
    val alcohol100g: Double,
    @SerializedName("alcohol_serving")
    val alcoholServing: Double,
    @SerializedName("alcohol_unit")
    val alcoholUnit: String,
    @SerializedName("alcohol_value")
    val alcoholValue: Double,
    val carbohydrates: Double,
    @SerializedName("carbohydrates_100g")
    val carbohydrates100g: Double,
    @SerializedName("carbohydrates_serving")
    val carbohydratesServing: Double,
    @SerializedName("carbohydrates_unit")
    val carbohydratesUnit: String,
    @SerializedName("carbohydrates_value")
    val carbohydratesValue: Double,
    val energy: Double,
    @SerializedName("energy-kcal")
    val energyKcal: Double,
    @SerializedName("energy-kcal_100g")
    val energyKcal100g: Double,
    @SerializedName("energy-kcal_serving")
    val energyKcalServing: Double,
    @SerializedName("energy-kcal_unit")
    val energyKcalUnit: String?,
    @SerializedName("energy-kcal_value")
    val energyKcalValue: Double,
    @SerializedName("energy-kcal_value_computed")
    val energyKcalValueComputed: Double,
    val energy_100g: Double,
    val energy_serving: Double,
    @SerializedName("energy_unit")
    val energyUnit: String,
    @SerializedName("energy_value")
    val energyValue: Double,
    val fat: Double,
    @SerializedName("fat_100g")
    val fat100g: Double,
    @SerializedName("fat_serving")
    val fatServing: Double,
    @SerializedName("fat_unit")
    val fatUnit: String,
    @SerializedName("fat_value")
    val fatValue: Double,
    @SerializedName("fruits-vegetables-legumes-estimate-from-ingredients_100g")
    val fruitsVegetablesLegumesEstimateFromIngredients100g: Double,
    @SerializedName("fruits-vegetables-legumes-estimate-from-ingredients_serving")
    val fruitsVegetablesLegumesEstimateFromIngredientsServing: Double,
    @SerializedName("fruits-vegetables-nuts-estimate-from-ingredients_100g")
    val fruitsVegetablesNutsEstimateFromIngredients100g: Double,
    @SerializedName("fruits-vegetables-nuts-estimate-from-ingredients_serving")
    val fruitsVegetablesNutsEstimateFromIngredientsServing: Double,
    @SerializedName("nutrition-score-fr")
    val nutritionScoreFr: Int,
    @SerializedName("nutrition-score-fr_100g")
    val nutritionScoreFr100g: Int,
    val proteins: Double,
    @SerializedName("proteins_100g")
    val proteins100g: Double,
    @SerializedName("proteins_serving")
    val proteinsServing: Double,
    @SerializedName("proteins_unit")
    val proteinsUnit: String,
    @SerializedName("proteins_value")
    val proteinsValue: Double,
    val salt: Double,
    @SerializedName("salt_100g")
    val salt100g: Double,
    @SerializedName("salt_serving")
    val saltServing: Double,
    @SerializedName("salt_unit")
    val saltUnit: String,
    @SerializedName("salt_value")
    val saltValue: Double,
    val saturated_fat: Double,
    @SerializedName("saturated-fat_100g")
    val saturatedFat100g: Double,
    @SerializedName("saturated-fat_serving")
    val saturatedFatServing: Double,
    @SerializedName("saturated-fat_unit")
    val saturatedFatUnit: String?,
    @SerializedName("saturated-fat_value")
    val saturatedFatValue: Double,
    val sodium: Double,
    @SerializedName("sodium_100g")
    val sodium100g: Double,
    @SerializedName("sodium_serving")
    val sodiumServing: Double,
    @SerializedName("sodium_unit")
    val sodiumUnit: String,
    @SerializedName("sodium_value")
    val sodiumValue: Double,
    val sugars: Double,
    @SerializedName("sugars_100g")
    val sugars100g: Double,
    @SerializedName("sugars_serving")
    val sugarsServing: Double,
    @SerializedName("sugars_unit")
    val sugarsUnit: String,
    @SerializedName("sugars_value")
    val sugarsValue: Double
)
