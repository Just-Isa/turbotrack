package com.kidrich.turbotrack

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.kidrich.turbotrack.databinding.IngredientDetailActivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IngredientDetailActivity: AppCompatActivity() {

    private lateinit var binding: IngredientDetailActivityBinding
    private lateinit var informationLayout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = IngredientDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        informationLayout = this.findViewById<LinearLayout>(R.id.ingredient_detail_meal_layout)
        informationLayout.removeAllViews()

        val receivedList: List<Ingredient>? =
            intent.getParcelableArrayListExtra("ingredients")

        val db = MealDatabase.getInstance(applicationContext)

        val viewModelIngredient by viewModels<IngredientViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return IngredientViewModel(db.ingredientDao) as T
                    }
                }
            }
        )

        val viewModelMeal by viewModels<MealViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MealViewModel(db.mealDao, db.ingredientDao) as T
                    }
                }
            }
        )

        if (receivedList != null) {
            if (receivedList.isNotEmpty()) {
                receivedList.forEach {ingredient: Ingredient ->

                    val ingredientDetail = layoutInflater.inflate(R.layout.ingredient_meal_detail, null, false)

                    ingredientDetail.findViewById<TextView>(R.id.meal_ingredient_detail_name).text = ingredient.name
                    ingredientDetail.findViewById<TextView>(R.id.meal_ingredient_detail_calories).text = ingredient.calories.toString()+"cal"
                    ingredientDetail.findViewById<TextView>(R.id.meal_ingredient_detail_grams).text = ingredient.grams.toString()+"g"
                    ingredientDetail.findViewById<TextView>(R.id.meal_ingredient_detail_salt).text = String.format("%.2f",(ingredient.salt100g?.div(100)?.times(ingredient.grams)))+"g Salt"
                    ingredientDetail.findViewById<TextView>(R.id.meal_ingredient_detail_fat).text = String.format("%.2f",(ingredient.fat100g?.div(100)?.times(ingredient.grams)))+"g Fat"
                    ingredientDetail.findViewById<TextView>(R.id.meal_ingredient_detail_protein).text =String.format("%.2f",(ingredient.proteins100g?.div(100)?.times(ingredient.grams)))+"g Protein"
                    ingredientDetail.findViewById<TextView>(R.id.meal_ingredient_detail_sugar).text = String.format("%.2f",(ingredient.sugars100g?.div(100)?.times(ingredient.grams)))+"g Sugar"

                    ingredientDetail.findViewById<ImageView>(R.id.meal_ingredient_detail_remove_ingredient).setOnClickListener {
                        if (receivedList.size == 1) {
                            CoroutineScope(Dispatchers.Main).launch {
                                viewModelMeal.onEvent(MealEvent.DeleteMealById(ingredient.mealId))
                                val chart = (this@IngredientDetailActivity as? MainScreenActivity)?.findViewById<BarChart>(R.id.fragment_verticalbarchart_chart)
                                chart?.highlightValues(null)
                                finish()
                            }
                        } else {
                            removeView(ingredientDetail)
                            CoroutineScope(Dispatchers.Main).launch {
                                viewModelIngredient.onEvent(IngredientEvent.DeleteIngredient(ingredient))
                            }
                        }
                    }


                    informationLayout.addView(ingredientDetail)
                }
            } else {
                finish()
            }
        } else {
            finish()
        }

        binding.mealIngredientCloseButton.setOnClickListener {
            finish()
        }
    }

    private fun removeView(view: View ) {
        informationLayout.removeView(view)
    }
}