package com.kidrich.turbotrack

import android.app.AlertDialog
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
            addView()
        }


        //get something from storage
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

                    if (!editName.text.isNullOrBlank() && !editCalories.text.isNullOrBlank()) {
                        ingredientsToSubmit.add(
                            Ingredient(
                                name = editName.text.toString(),
                                calories = editCalories.text.toString().toInt(),
                                mealId = meal.mealId
                            )
                        )
                    }
                }
                Log.d("warum2", ingredientsToSubmit.toString())


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
                    this.showAlertDialog("Please make sure to fill in everything!")
                }
            }

        }

        binding.mealIngredientCloseButton.setOnClickListener {
            finish();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            }
        }
    }

    private fun addView() {
        val ingredientView = layoutInflater.inflate(R.layout.row_add_ingredient, null, false)

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
}