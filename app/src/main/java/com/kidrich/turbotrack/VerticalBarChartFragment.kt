package com.kidrich.turbotrack

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class VerticalBarChartFragment : Fragment() {

    private var chart: BarChart? = null
    private var mealsMappedToDays: ArrayList<Pair<String,MealWithIngredients>> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_verticalbarchart, container, false)

        val db  = MealDatabase.getInstance(requireActivity().applicationContext)

        val viewModelMeal by viewModels<MealViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MealViewModel(db.mealDao, db.ingredientDao) as T
                    }
                }
            }
        )

        chart = view.findViewById(R.id.fragment_verticalbarchart_chart)
        val activity = activity as? MainScreenActivity

        configureChartAppearance()
        if (activity != null) {
            prepareChartData(viewModelMeal, activity)
        }


        return view
    }

    private fun configureChartAppearance() {
        // CHART APPEARENCE

        chart?.description?.isEnabled = false;

        chart?.setDrawValueAboveBar(false);
        chart?.setScaleEnabled(false);
        chart?.setDrawGridBackground(false)
        chart?.setGridBackgroundColor(ColorTemplate.rgb("#363636"))

        chart!!.xAxis.textColor = ResourcesCompat.getColor(getResources(), R.color.white, null)

        chart!!.axisLeft.setDrawZeroLine(true)

        val xAxis: XAxis = chart!!.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return DAYS[value.toInt()]
            }
        }
        val axisLeft: YAxis = chart!!.axisLeft
        axisLeft.granularity = 10f
        axisLeft.axisMinimum = 0.toFloat()
        axisLeft.setDrawLabels(false);


        val axisRight: YAxis = chart!!.axisRight
        axisRight.granularity = 10f
        axisRight.axisMinimum = 0.toFloat()
        axisRight.setDrawLabels(false);
    }


    private fun prepareChartData(viewModelMeal: MealViewModel, activity: MainScreenActivity) {
        lifecycleScope.launch { // assuming you're inside an Activity or Fragment
            viewModelMeal.state.collect { mealState ->

                val today = LocalDate.now()
                val monday = today.with(DayOfWeek.MONDAY)
                val datesOfWeek = (0 until 7).map { monday.plusDays(it.toLong()) }
                val formattedDates = datesOfWeek.map { it.format(DateTimeFormatter.ISO_DATE) }

                val values = arrayListOf<BarEntry>()
                mealsMappedToDays = arrayListOf()

                for (i in 0 until 7) {
                    // FILTER MEALS TO TODAY
                    val relevantMeals = mealState.meals.filter { meals -> meals.meal.timestamp == formattedDates[i] }
                    Log.d("relevantMeals", relevantMeals.toString())
                    if (relevantMeals.isEmpty()) {
                        values.add(BarEntry(i.toFloat(), 0f))
                        continue
                    }
                    var totalCalsForDay = 0
                    relevantMeals.forEach { mealWithIngredients: MealWithIngredients -> totalCalsForDay += mealWithIngredients.ingredients.sumOf { ingredients -> ingredients.calories } }
                    relevantMeals.forEach { mealWithIngredients: MealWithIngredients -> mealsMappedToDays.add(Pair(formattedDates[i], mealWithIngredients))}
                    Log.d("mealsMappedToDays", mealsMappedToDays.toString())

                    values.add(BarEntry(i.toFloat(), totalCalsForDay.toFloat()))

                    chart?.setOnChartValueSelectedListener(BarChartOnChartValueSelectedListener(mealsMappedToDays, activity, formattedDates))
                }
                updateChart(values)
            }
        }
    }

    private fun updateChart(values: ArrayList<BarEntry>) {
        Log.d("chart", values.toString())

        val set1: BarDataSet = BarDataSet(values, SET_LABEL)
        set1.color = ColorTemplate.rgb("#98FF98")
        val dataSets: ArrayList<IBarDataSet> = arrayListOf()
        dataSets.add(set1)
        val data: BarData = BarData(dataSets)

        // Give chart dataset
        data.setValueTextSize(12f)
        data.setValueTextColor(ColorTemplate.rgb("#ffffff"))
        chart?.legend?.isEnabled = true
        chart?.legend?.textSize = 11f
        chart?.legend?.textColor = ColorTemplate.rgb(("#ffffff"))
        chart!!.data = data
        chart!!.barData.setValueTextColor(ColorTemplate.rgb("#000000"))
        chart?.barData?.setValueFormatter(CaloryValueFormatter())
        chart!!.invalidate()
    }
    companion object {
        private const val MAX_X_VALUE = 7
        private const val MAX_Y_VALUE = 50
        private const val MIN_Y_VALUE = 5
        private const val SET_LABEL = "Daily Calories"
        private val DAYS = arrayOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    }

}

public class CaloryValueFormatter : ValueFormatter() {
    private var mFormat: DecimalFormat =  DecimalFormat("###,###,##0")

    override fun getFormattedValue(value: Float): String {
        return mFormat.format(value)
    }
}

private class BarChartOnChartValueSelectedListener : OnChartValueSelectedListener  {
    private var mealsMappedToDays: ArrayList<Pair<String,MealWithIngredients>> = arrayListOf()
    private lateinit var activity: MainScreenActivity
    private lateinit var formattedDates: List<String>

    constructor(mealsMappedToDays: ArrayList<Pair<String,MealWithIngredients>>, activity: MainScreenActivity, formattedDates: List<String>) {
        this.formattedDates = formattedDates
        this.mealsMappedToDays = mealsMappedToDays
        this.activity = activity
    }
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val informationLayout = activity.findViewById<LinearLayout>(R.id.meal_clicked_information)
        informationLayout.removeAllViews()

        mealsMappedToDays.filter { mealsMappedToDays -> mealsMappedToDays.first == formattedDates[e?.x?.toInt()!!]}.forEach { pair ->
            val button = AppCompatButton(activity)
            button.text = pair.first + " - " + pair.second.meal.name

            button.setOnClickListener {
                onButtonClicked(pair.second.ingredients)
            }

            button.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            informationLayout.addView(button)
        }
    }

    private fun onButtonClicked(ingredients: List<Ingredient>) {
        Log.d("test", ingredients.toString())
        // Add your logic for handling button click with specific ingredients
    }

    override fun onNothingSelected() {
        val informationLayout = activity.findViewById<LinearLayout>(R.id.meal_clicked_information)
        informationLayout.removeAllViews()
    }
}