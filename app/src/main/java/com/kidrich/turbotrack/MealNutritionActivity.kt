package com.kidrich.turbotrack

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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


            val totalFat = receivedList.sumOf {
                ((it.fat100g?.div(100) ?: 0.0 / 100) * (it.grams))
            }

            val totalSugar = receivedList.sumOf {
                ((it.sugars100g?.div(100) ?: 0.0 / 100) * (it.grams))
            }

            val totalSalt = receivedList.sumOf {
                ((it.salt100g?.div(100) ?: 0.0 / 100) * (it.grams))
            }

            val totalProtein = receivedList.sumOf {
                ((it.proteins100g?.div(100) ?: 0.0 / 100) * (it.grams))
            }

            // Customize the chart appearance
            pieChart.setUsePercentValues(true);
            pieChart.getDescription().setEnabled(false);
            pieChart.setExtraOffsets(5f, 10f, 5f, 5f);
            pieChart.setDragDecelerationFrictionCoef(0.95f);
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleColor(Color.WHITE);
            pieChart.setTransparentCircleRadius(61f);

            val entries: ArrayList<PieEntry> = arrayListOf()


            if(totalFat.toFloat() > 0.1f) {
                entries.add(PieEntry(totalFat.toFloat(), "Fat", ))
            }

            if(totalSugar.toFloat() > 0.1f) {
                entries.add(PieEntry(totalSugar.toFloat(), "Sugar"))
            }

            if(totalSalt.toFloat() > 0.1f) {
                entries.add(PieEntry(totalSalt.toFloat(), "Salt"))
            }

            if(totalProtein.toFloat() > 0.1f) {
                entries.add(PieEntry(totalProtein.toFloat(), "Protein"))
            }

            val dataSet: PieDataSet = PieDataSet(entries, "Meal-Nutrients")
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
        Log.d("nutrition", e.toString())
        if (e is PieEntry) {
            showAlertDialog(e.label + " - " + e.value + "g")
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

    override fun onNothingSelected() {
        TODO("Not yet implemented")
    }

}

class CustomValueFormatter : ValueFormatter() {

    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        // Return the actual value as a string
        return value.toInt().toString()
    }
}