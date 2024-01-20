package com.kidrich.turbotrack

import android.app.AlertDialog
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kidrich.turbotrack.databinding.ActivityMealFormBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class AddMealActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMealFormBinding

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

        //get something from storage
        binding.mealIngredientAddButton.setOnClickListener {
            if(!binding.mealIngredientName.text.isNullOrBlank() &&
                !binding.mealIngredientCalories.text.isNullOrBlank() ) {

                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        withContext(Dispatchers.IO) {
                            withContext(Dispatchers.IO) {
                                var meal: Meal = Meal(
                                    timestamp = getFormattedDay(),
                                    isSnack = false
                                )

                                var ingredients = arrayListOf<Ingredient>(
                                    Ingredient(name = binding.mealIngredientName.text.toString(), calories = binding.mealIngredientCalories.text.toString().toInt(), mealId = meal.mealId),
                                )

                                viewModelMeal.onEvent(MealEvent.InsertMealWithIngredients(meal, ingredients))
                            }
                        }
                    } catch(e: Exception) {
                        Log.d("Saving meal Error!", e.toString())
                    }
                }
                finish();
            } else {
                this.showAlertDialog("Please make sure to fill in everything!")
            }
        }

        binding.mealIngredientCloseButton.setOnClickListener {
            finish();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            }
        }
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
}