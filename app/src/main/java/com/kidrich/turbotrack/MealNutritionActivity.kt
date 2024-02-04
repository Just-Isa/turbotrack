package com.kidrich.turbotrack

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.kidrich.turbotrack.databinding.ActivityMealNutritionsBinding

class MealNutritionActivity: AppCompatActivity(), OnChartValueSelectedListener {

    private lateinit var binding: ActivityMealNutritionsBinding
    private val proteinList: ArrayList<Pair<Ingredient, Double>> = arrayListOf()
    private val fatList: ArrayList<Pair<Ingredient, Double>> = arrayListOf()
    private val sugarList: ArrayList<Pair<Ingredient, Double>> = arrayListOf()
    private val saltList: ArrayList<Pair<Ingredient, Double>> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMealNutritionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receivedList: List<Ingredient>? =
            intent.getParcelableArrayListExtra("ingredients")

        val mealName: String? = intent.getStringExtra("mealName")
        val mealCals: String? = intent.getStringExtra("mealCals")


        val pieChart: PieChart = binding.mealNutritionalInfoPiechart

        if (mealName.isNullOrBlank() || mealCals.isNullOrBlank() || receivedList.isNullOrEmpty()) {
            finish()
        }


        if (!mealCals.isNullOrBlank() && !mealName.isNullOrBlank() && !receivedList.isNullOrEmpty()) {
            binding.mealNutritionalInfoMealcals.text = mealCals + " calories total"
            binding.mealNutritionalInfoMealname.text = mealName

            receivedList.forEach {
                if (it.fat100g != 0.0 && it.fat100g != null && it.fat100g >= 0.5) {
                    val fatAmount = (((it.fat100g.div(100) ?: (0.0 / 100)) * (it.grams)))
                    fatList.add(Pair(it, fatAmount))
                }
                if (it.sugars100g != 0.0 && it.sugars100g != null && it.sugars100g >= 1) {
                    val sugarAmount = (((it.sugars100g.div(100) ?: (0.0 / 100)) * (it.grams)))
                    sugarList.add(Pair(it, sugarAmount))
                }
                if (it.salt100g != 0.0 && it.salt100g != null && it.salt100g >= 0.01) {
                    val saltAmount = (((it.salt100g.div(100) ?: (0.0 / 100)) * (it.grams)))
                    saltList.add(Pair(it, saltAmount))
                }
                if (it.proteins100g != 0.0 && it.proteins100g != null && it.proteins100g >= 0.3) {
                    val proteinAmount = (((it.proteins100g.div(100) ?: (0.0 / 100)) * (it.grams)))
                    proteinList.add(Pair(it, proteinAmount))
                }
            }


            val totalFat = fatList.sumOf {
                it.second
            }

            val totalSugar = sugarList.sumOf {
                it.second
            }

            val totalSalt = saltList.sumOf {
                it.second
            }

            val totalProtein = proteinList.sumOf {
                it.second
            }

            pieChart.setUsePercentValues(true);
            pieChart.description.isEnabled = false;
            pieChart.setExtraOffsets(5f, 10f, 5f, 5f);
            pieChart.dragDecelerationFrictionCoef = 0.95f;
            pieChart.isDrawHoleEnabled = true;
            pieChart.setHoleColor(Color.WHITE);
            pieChart.transparentCircleRadius = 61f;

            val entries: ArrayList<PieEntry> = arrayListOf()


            if(totalFat.toFloat() > 0.5f) {
                entries.add(PieEntry(totalFat.toFloat(), "Fat", ))
            }

            if(totalSugar.toFloat() > 0.8f) {
                entries.add(PieEntry(totalSugar.toFloat(), "Sugar"))
            }

            if(totalSalt.toFloat() > 0.4f) {
                entries.add(PieEntry(totalSalt.toFloat(), "Salt"))
            }

            if(totalProtein.toFloat() > 0.8f) {
                entries.add(PieEntry(totalProtein.toFloat(), "Protein"))
            }

            val dataSet: PieDataSet = PieDataSet(entries, "")
            dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList();
            dataSet.valueTextColor = Color.WHITE
            dataSet.valueTextSize = 15f


            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString() + "%"
                }
            }

            val data: PieData = PieData(dataSet)
            data.setValueTextColor(20)
            data.setValueTextColor(Color.WHITE)

            pieChart.setHoleColor(Color.BLACK);
            pieChart.setOnChartValueSelectedListener(this)
            pieChart.transparentCircleRadius = 0f
            pieChart.legend.textColor = Color.WHITE
            pieChart.data = data


        }

        binding.mealNutritionCloseButton.setOnClickListener {
            finish()
        }
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e is PieEntry) {
            when(e.label) {
                "Sugar" -> {
                    binding.mealNutritionalInfoSpecificText.text = e.value.toString() + "g total sugar\n\n"
                    sugarList.forEach {
                        binding.mealNutritionalInfoSpecificText.text =
                            binding.mealNutritionalInfoSpecificText.text.toString() + it.first.name + " - " + it.second.toString() + "g\n"
                    }
                }
                "Fat" -> {
                    binding.mealNutritionalInfoSpecificText.text = e.value.toString() + "g total fat\n\n"
                    fatList.forEach {
                        binding.mealNutritionalInfoSpecificText.text =
                            binding.mealNutritionalInfoSpecificText.text.toString() + it.first.name + " - " + it.second.toString() + "g\n"
                    }
                }
                "Protein" -> {
                    binding.mealNutritionalInfoSpecificText.text = e.value.toString() + "g total protein\n\n"
                    proteinList.forEach {
                        binding.mealNutritionalInfoSpecificText.text =
                            binding.mealNutritionalInfoSpecificText.text.toString() + it.first.name + " - " + it.second.toString() + "g\n"
                    }
                }
                "Salt" -> {
                    binding.mealNutritionalInfoSpecificText.text = e.value.toString() + "g total salt\n\n"
                    saltList.forEach {
                        binding.mealNutritionalInfoSpecificText.text =
                            binding.mealNutritionalInfoSpecificText.text.toString() + it.first.name + " - " + it.second.toString() + "g\n"
                    }
                }
            }

        }
    }

    override fun onNothingSelected() {
        binding.mealNutritionalInfoSpecificText.text = ""
    }

}

class CustomValueFormatter : ValueFormatter() {

    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        // Return the actual value as a string
        return value.toInt().toString()
    }
}