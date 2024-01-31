package com.kidrich.turbotrack

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kidrich.turbotrack.databinding.IngredientDetailActivityBinding

class IngredientDetailActivity: AppCompatActivity() {

    private lateinit var binding: IngredientDetailActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = IngredientDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val informationLayout = this.findViewById<LinearLayout>(R.id.ingreadient_detail_meal_layout)
        informationLayout.removeAllViews()

        val receivedList: List<Ingredient>? =
            intent.getParcelableArrayListExtra("ingredients")

        if (receivedList != null) {
            if (receivedList.isNotEmpty()) {
                receivedList.forEach {

                    val ingredientDetail = layoutInflater.inflate(R.layout.ingredient_meal_detail, null, false)

                    ingredientDetail.findViewById<TextView>(R.id.meal_ingredient_detail_name).text = it.name
                    ingredientDetail.findViewById<TextView>(R.id.meal_ingredient_detail_calories).text = it.calories.toString()+"cal"
                    ingredientDetail.findViewById<TextView>(R.id.meal_ingredient_detail_grams).text = it.grams.toString()+"g/ml"


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
}