package com.kidrich.turbotrack

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
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
        val weekly: Boolean = intent.getBooleanExtra("WEEKLY", false)


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
            val textView = TextView(this)
            textView.gravity = Gravity.CENTER
            textView.textSize = 20f

            binding.mealNutritionalInfoText.removeAllViews()
            when (e.label) {
                "Sugar" -> {
                    textView.text = String.format("%.2f", e.value) + "g total sugar"
                    binding.mealNutritionalInfoText.addView(textView)
                    sugarList.forEach {
                        val textViewSugar = TextView(this)
                        textViewSugar.text =  it.first.name + " - " + String.format("%.2f", it.second) + "g"
                        textViewSugar.textSize = 18f
                        binding.mealNutritionalInfoText.addView(textViewSugar)
                    }
                }
                "Fat" -> {
                    textView.text = String.format("%.2f", e.value) + "g total fat"
                    binding.mealNutritionalInfoText.addView(textView)
                    fatList.forEach {
                        val textViewFat = TextView(this)
                        textViewFat.text = it.first.name + " - " + String.format("%.2f", it.second) + "g"
                        textViewFat.textSize = 18f
                        binding.mealNutritionalInfoText.addView(textViewFat)
                    }
                }
                "Protein" -> {
                    textView.text =String.format("%.2f", e.value) + "g total protein"
                    binding.mealNutritionalInfoText.addView(textView)
                    proteinList.forEach {
                        val textViewProtein = TextView(this)
                        textViewProtein.text = it.first.name + " - " + String.format("%.2f", it.second) + "g"
                        textViewProtein.textSize = 18f
                        binding.mealNutritionalInfoText.addView(textViewProtein)
                    }
                }
                "Salt" -> {
                    textView.text =  String.format("%.2f", e.value) + "g total salt"
                    binding.mealNutritionalInfoText.addView(textView)
                    saltList.forEach {
                        val textViewSalt = TextView(this)
                        textViewSalt.text = it.first.name + " - " + String.format("%.2f", it.second) + "g"
                        textViewSalt.textSize = 18f
                        binding.mealNutritionalInfoText.addView(textViewSalt)
                    }
                }
            }

        }
    }

    override fun onNothingSelected() {
        binding.mealNutritionalInfoText.removeAllViews()
    }

}

class CustomValueFormatter : ValueFormatter() {

    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        // Return the actual value as a string
        return value.toInt().toString()
    }
}