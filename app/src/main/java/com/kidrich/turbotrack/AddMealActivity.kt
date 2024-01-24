package com.kidrich.turbotrack

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
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kidrich.turbotrack.databinding.ActivityMealFormBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class AddMealActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMealFormBinding
    private var ingredientList : ArrayList<View> = arrayListOf()
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
                Log.d("cameratest", "Cant open cause no permission!!-")
            } else {
                scanIngredient()
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
                    isSnack = this.binding.switchSnack.isActivated
                )
                Log.d("warum1", ingredientList.toString())
                ingredientList.forEach {
                    val editName: EditText = it.findViewById(R.id.meal_ingredient_name)
                    val editCalories: EditText = it.findViewById(R.id.meal_ingredient_calories)
                    val editAmount: EditText = it.findViewById(R.id.meal_ingredient_calories_grams)

                    if (!editName.text.isNullOrBlank() && !editCalories.text.isNullOrBlank() && !editAmount.text.isNullOrBlank()) {
                        ingredientsToSubmit.add(
                            Ingredient(
                                name = editName.text.toString(),
                                calories = ((editCalories.text.toString().toFloat() / 100) * (editAmount.text.toString().toFloat())).toInt(),
                                mealId = meal.mealId
                            )
                        )
                    }
                }

                if (ingredientsToSubmit.size == ingredientList.size) {
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

    @Deprecated("Deprecated in Java")
    private fun scanIngredient() {
        startActivityForResult(Intent(this, CameraViewActivity::class.java), REQUEST_CODE_IMAGE)
        Log.d("Scan igredient", "TODO")
    }

    private fun addView(kCal100: String?, genericName: String?) {
        val ingredientView = layoutInflater.inflate(R.layout.row_add_ingredient, null, false)
        val caloriesPerHundred = ingredientView.findViewById<EditText>(R.id.meal_ingredient_calories)

        if (kCal100 != null) {
            caloriesPerHundred.setText(kCal100)
        }

        if (genericName != null) {
            ingredientView.findViewById<EditText>(R.id.meal_ingredient_name).setText(genericName)
        }

        ingredientView.findViewById<ImageView>(R.id.meal_ingredient_remove).setOnClickListener {
            removeView(ingredientView)
        }

        ingredientList.add(ingredientView)
        binding.layoutList.addView(ingredientView)

    }

    private fun removeView(view: View) {
        ingredientList.remove(view)
        binding.layoutList.removeView(view)
    }

    private fun showAlertDialog(text: String) {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(text)
            .setPositiveButton("Alrighty") {_, _ ->
                //pass
            }.create().show()
    }

    fun getFormattedDay(): String{
        return SimpleDateFormat("yyyy-MM-dd").format(Date());
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            val kCal100: String? = data?.getStringExtra("kcalperhundred")
            val genericName: String? = data?.getStringExtra("genericname")
            if (!kCal100.isNullOrBlank()) {
                addView(kCal100, genericName)
            }
        }
    }
}