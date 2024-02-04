package com.kidrich.turbotrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.kidrich.turbotrack.databinding.ActivityMainscreenBinding
import kotlinx.coroutines.launch

class MainScreenActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainscreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db  = MealDatabase.getInstance(this)

        val viewModelMeal by viewModels<MealViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MealViewModel(db.mealDao, db.ingredientDao) as T
                    }
                }
            }
        )

        setClickListeners(viewModelMeal)

        val fragment = VerticalBarChartFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setClickListeners(viewModelMeal: MealViewModel) {
        binding.addMealButton.setOnClickListener {
            startActivity(Intent(this, AddMealActivity::class.java))
        }


        binding.weeklyNutritionButton.setOnClickListener {

                val allIngredientsForWeek: ArrayList<Ingredient> = ArrayList<Ingredient>()
                lifecycleScope.launch {
                    viewModelMeal.state.collect { mealState ->
                        try {
                            for (array in mealState.meals) {
                                allIngredientsForWeek.addAll(array.ingredients.toList())
                            }
                            val intent =
                                Intent(this@MainScreenActivity, MealNutritionActivity::class.java)
                            intent.putParcelableArrayListExtra("ingredients", allIngredientsForWeek)
                            intent.putExtra("mealCals",
                                allIngredientsForWeek.sumOf { ingredient -> ingredient.calories }
                                    .toString()
                            )
                            intent.putExtra("mealName", "Weekly Nutrition")
                            this@MainScreenActivity.startActivity(intent)
                        } catch (e: Exception) {
                        Log.e("Error", "Error starting MealNutritionActivity", e)
                    }
                }
            }
        }
    }
}
