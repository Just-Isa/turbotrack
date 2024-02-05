package com.kidrich.turbotrack

import Nutriments
import OpenFoodFactsApiTask
import ProductResponse
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.kidrich.turbotrack.databinding.ActivityMealFormBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class AddMealActivity: AppCompatActivity(), ApiTaskCallback {

    private lateinit var binding: ActivityMealFormBinding
    private var ingredientInputList : ArrayList<Pair<View, Nutriments?>> = arrayListOf()
    private val REQUEST_CODE_IMAGE = 6969420
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = MealDatabase.getInstance(applicationContext)

        val viewModelMeal by viewModels<MealViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MealViewModel(db.mealDao, db.ingredientDao) as T
                    }
                }
            }
        )

        binding = ActivityMealFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mealAddIngredientButton.setOnClickListener {
                addView(null, null)
        }

        binding.mealScanIngredientButton.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,  arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_IMAGE)
            } else {
                scanCode()
            }

        }

        binding.mealSubmitButton.setOnClickListener {
            var ingredientsToSubmit: ArrayList<Ingredient> = arrayListOf()

            if (binding.mealAddMealName.text.isNullOrBlank()) {
                this.showAlertDialog("Please enter a Name for the Meal")
            } else {
                var meal: Meal = Meal(
                    name = this.binding.mealAddMealName.text.toString(),
                    timestamp = getFormattedDay(),
                    isSnack = this.binding.switchSnack.isChecked
                )
                ingredientInputList.forEach {
                    val editName: EditText = it.first.findViewById(R.id.meal_ingredient_name)
                    val editCalories: EditText = it.first.findViewById(R.id.meal_ingredient_calories)
                    val editAmount: EditText = it.first.findViewById(R.id.meal_ingredient_calories_grams)
                    val editSugar: EditText = it.first.findViewById(R.id.meal_ingredient_sugar)
                    val editProtein: EditText = it.first.findViewById(R.id.meal_ingredient_protein)
                    val editFat: EditText = it.first.findViewById(R.id.meal_ingredient_fat)
                    val editSalt: EditText = it.first.findViewById(R.id.meal_ingredient_salt)

                    if (!editName.text.isNullOrBlank() &&
                        !editCalories.text.isNullOrBlank() &&
                        !editAmount.text.isNullOrBlank() &&
                        !editFat.text.isNullOrBlank() &&
                        !editSugar.text.isNullOrBlank() &&
                        !editProtein.text.isNullOrBlank()
                        ) {
                        if (it.second != null && it.second!!.energyKcal.toFloat() != 0.0f) {
                            var calories = ((it.second!!.energyKcal.toFloat() / 100) * (editAmount.text.toString().toFloat())).toInt()
                            if (calories < 1) {
                                calories = 1
                            }
                            ingredientsToSubmit.add(
                                Ingredient(
                                    name = editName.text.toString(),
                                    calories = calories,
                                    mealId = meal.mealId,
                                    grams = Integer.parseInt(editAmount.text.toString()),
                                    alcohol = it.second?.alcohol,
                                    alcohol100g = it.second?.alcohol100g,
                                    alcoholServing = it.second?.alcoholServing,
                                    alcoholUnit = it.second?.alcoholUnit,
                                    alcoholValue = it.second?.alcoholValue,
                                    carbohydrates = it.second?.carbohydrates,
                                    carbohydrates100g = it.second?.carbohydrates100g,
                                    carbohydratesServing = it.second?.carbohydratesServing,
                                    carbohydratesUnit = it.second?.carbohydratesUnit,
                                    carbohydratesValue = it.second?.carbohydratesValue,
                                    energy = it.second?.energy,
                                    energy_100g = it.second?.energy_100g,
                                    energy_serving = it.second?.energy_serving,
                                    energyKcal = it.second?.energyKcal,
                                    energyKcal100g = it.second?.energyKcal100g,
                                    energyKcalServing = it.second?.energyKcalServing,
                                    energyKcalUnit = it.second?.energyKcalUnit,
                                    energyKcalValue = it.second?.energyKcalValue,
                                    energyKcalValueComputed = it.second?.energyKcalValueComputed,
                                    energyUnit = it.second?.energyUnit,
                                    energyValue = it.second?.energyValue,
                                    fat = it.second?.fat,
                                    fat100g = editFat.text.toString().toDouble(),
                                    fatServing = it.second?.fatServing,
                                    fatUnit = it.second?.fatUnit,
                                    fatValue = it.second?.fatValue,
                                    proteins = it.second?.proteins,
                                    nutritionScoreFr = it.second?.nutritionScoreFr,
                                    nutritionScoreFr100g = it.second?.nutritionScoreFr,
                                    proteins100g = editProtein.text.toString().toDouble(),
                                    proteinsServing = it.second?.proteinsServing,
                                    proteinsUnit = it.second?.proteinsUnit,
                                    proteinsValue = it.second?.proteinsValue,
                                    salt = it.second?.salt,
                                    salt100g = editSalt.text.toString().toDouble(),
                                    saltServing = it.second?.saltServing,
                                    saltUnit = it.second?.saltUnit,
                                    saltValue = it.second?.saltValue,
                                    sugars = it.second?.sugars,
                                    sugars100g = editSugar.text.toString().toDouble(),
                                    sugarsServing = it.second?.sugarsServing,
                                    sugarsUnit = it.second?.sugarsUnit,
                                    sugarsValue = it.second?.sugarsValue
                                )
                            )
                        } else {
                            var calories = ((editCalories.text.toString().toFloat() / 100) * (editAmount.text.toString().toFloat())).toInt()
                            if (calories < 1) {
                                calories = 1
                            }
                            ingredientsToSubmit.add(
                                Ingredient(
                                    name = editName.text.toString(),
                                    calories = calories,
                                    mealId = meal.mealId,
                                    grams = Integer.parseInt(editAmount.text.toString()),
                                    fat100g = editFat.text.toString().toDouble(),
                                    proteins100g = editProtein.text.toString().toDouble(),
                                    sugars100g = editSugar.text.toString().toDouble(),
                                    salt100g = editSalt.text.toString().toDouble()
                                )
                            )
                        }
                    }
                }

                if (ingredientsToSubmit.size == ingredientInputList.size) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelMeal.onEvent(
                            MealEvent.InsertMealWithIngredients(
                                meal,
                                ingredientsToSubmit
                            )
                        )
                        finish()
                    }
                } else {
                    this.showAlertDialog("Please make sure to fill in every Ingredient!")
                }
            }

        }

        binding.mealIngredientCloseButton.setOnClickListener {
            finish();
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == this.REQUEST_CODE_IMAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.scanCode()
            } else {
                showAlertDialog("You need Camera Permissions to scan barcodes!" +
                        "")
            }
        }
    }


    private fun addView(nutriments: Nutriments?, genericName: String?) {
        val ingredientView = layoutInflater.inflate(R.layout.row_add_ingredient, null, false)
        val caloriesPerHundred = ingredientView.findViewById<EditText>(R.id.meal_ingredient_calories)
        val sugarPerHundred = ingredientView.findViewById<EditText>(R.id.meal_ingredient_sugar)
        val proteinPerHundred = ingredientView.findViewById<EditText>(R.id.meal_ingredient_protein)
        val fatPerHundred = ingredientView.findViewById<EditText>(R.id.meal_ingredient_fat)
        val saltPerHundred = ingredientView.findViewById<EditText>(R.id.meal_ingredient_salt)
        var allDataPresent: Boolean = true;
        if (genericName != null) {
            ingredientView.findViewById<EditText>(R.id.meal_ingredient_name).setText(genericName)
        }

        if (nutriments != null) {
            if (nutriments.energyKcal100g != 0.0) {
                caloriesPerHundred.setText(nutriments.energyKcal100g.toString())

            } else {
                allDataPresent = false;
            }

            if (nutriments.sugars100g != 0.0) {
                sugarPerHundred.setText(nutriments.sugars100g.toString())
            } else {
                allDataPresent = false;
            }

            if (nutriments.proteins100g != 0.0) {
                proteinPerHundred.setText(nutriments.proteins100g.toString())
            } else {
                allDataPresent = false;
            }

            if (nutriments.fat100g != 0.0) {
                fatPerHundred.setText(nutriments.fat100g.toString())
            } else {
                allDataPresent = false;
            }

            if (nutriments.salt100g != 0.0) {
                saltPerHundred.setText(nutriments.fat100g.toString())
            } else {
                allDataPresent = false;
            }

            if (!allDataPresent) {
                showAlertDialog("BarCode data didn't include all the Data, please fill in by hand!")
            }


            ingredientInputList.add(Pair(ingredientView, nutriments));
            ingredientView.findViewById<ImageView>(R.id.meal_ingredient_remove).setOnClickListener {
                removeView(Pair(ingredientView, nutriments))
            }
        } else {
            ingredientInputList.add(Pair(ingredientView, null))

            ingredientView.findViewById<ImageView>(R.id.meal_ingredient_remove).setOnClickListener {
                removeView(Pair(ingredientView, null))
            }
        }

        binding.layoutList.addView(ingredientView)

    }

    private fun removeView(pair: Pair<View, Nutriments?> ) {
        ingredientInputList.remove(pair)
        binding.layoutList.removeView(pair.first)
    }

    private fun showAlertDialog(text: String) {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(text)
            .setPositiveButton("Alrighty") {_, _ ->
            }.create().show()
    }

    fun getFormattedDay(): String{
        return SimpleDateFormat("yyyy-MM-dd").format(Date());
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val apiTask = OpenFoodFactsApiTask(this)
                apiTask.execute(result.contents)
            } else {
                showAlertDialog("No qr code found!")
            }
        }
    }

    private fun scanCode() {
        val integrator: IntentIntegrator = IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct::class.java)
        integrator.setOrientationLocked(true)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scanning BarCode")
        integrator.initiateScan()
    }

    override fun onApiTaskComplete(result: ProductResponse?) {
        if (result != null) {
            val kCal100 = result.product.nutriments.energyKcal100g.toString();
            val genericName = result.product.product_name
            if (kCal100.isNotBlank() && genericName.isNotBlank()) {
                addView(result.product.nutriments, genericName)
            }
        } else {
            showAlertDialog("Something went wrong with the api call!")
        }
    }

    override fun onApiTaskError() {
        Log.e("api","Error on API")
    }

}