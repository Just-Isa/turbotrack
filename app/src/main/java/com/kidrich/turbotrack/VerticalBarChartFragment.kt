package com.kidrich.turbotrack

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
            prepareChartData(viewModelMeal, activity, inflater)
        }
        return view
    }


    private fun onNutritionalInfoButtonClicked(activity: MainScreenActivity, calories: Int, mealName: String, ingredients: List<Ingredient>) {
        val intent = Intent(activity, MealNutritionActivity::class.java)
        intent.putParcelableArrayListExtra("ingredients", ArrayList(ingredients))
        intent.putExtra("mealCals", calories.toString())
        intent.putExtra("mealName", mealName)
        activity.startActivity(intent)
    }


    private fun prepareGeneralData(activity: MainScreenActivity, mealViewModel: MealViewModel, formattedDates: List<String>, inflater: LayoutInflater) {
        activity.findViewById<LinearLayout>(R.id.meal_clicked_information).visibility = View.GONE

        val relevantMeals = mealViewModel.state.value.meals.filter { meals ->
            formattedDates.contains(meals.meal.timestamp) && !meals.meal.isSnack
        }


        val relevantSnacks = mealViewModel.state.value.meals
            .filter { meals ->
                formattedDates.contains(meals.meal.timestamp) && meals.meal.isSnack
            }
            .groupBy { it.meal.name } // Group meals by name
            .map { (name, mealsList) -> name to mealsList.size } // Map each group to Pair(name, count)
            .sortedByDescending { it.second } // Sort by count in descending order

        activity.findViewById<LinearLayout>(R.id.fave_snacks_last_meal_container).visibility = View.VISIBLE

        val lastMealsScrollView = activity.findViewById<LinearLayout>(R.id.scroll_view_last_meals)
        val faveSnacksScrollView = activity.findViewById<LinearLayout>(R.id.scroll_view_fave_snacks)

        lastMealsScrollView.removeAllViews()
        faveSnacksScrollView.removeAllViews()

        for (mealWithIngredients in relevantMeals) {
            val mealDetailButton = inflater.inflate(R.layout.row_show_meal_name_and_edit, null, false)
            mealDetailButton.findViewById<AppCompatButton>(R.id.last_meals_meal_name).text = mealWithIngredients.meal.name

            if (
                mealWithIngredients.ingredients.any { it.fat100g != 0.0 } ||
                mealWithIngredients.ingredients.any { it.proteins100g != 0.0 } ||
                mealWithIngredients.ingredients.any { it.salt100g != 0.0 } ||
                mealWithIngredients.ingredients.any { it.sugars100g != 0.0 } )
            {
                mealDetailButton.findViewById<AppCompatButton>(R.id.last_meals_meal_name).setOnClickListener {
                    onNutritionalInfoButtonClicked( activity, mealWithIngredients.ingredients.sumOf { ingredient -> ingredient.calories }, mealWithIngredients.meal.name , mealWithIngredients.ingredients)
                }
            } else {
                mealDetailButton.findViewById<AppCompatButton>(R.id.last_meals_meal_name).setOnClickListener {
                    AlertDialog.Builder(this.activity)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("No Nutritional Data present!")
                        .setPositiveButton("Alrighty") {_, _ ->
                        }.create().show()
                }
                mealDetailButton.findViewById<AppCompatButton>(R.id.last_meals_meal_name).background = activity.resources.getDrawable(R.drawable.add_ingredient_button_greyed_out_right)
            }
            lastMealsScrollView.addView(mealDetailButton)
        }

        for ((mealName, mealCount) in relevantSnacks) {
            val snackDetailButton = inflater.inflate(R.layout.row_show_snack_name_and_edit, null, false)
            snackDetailButton.findViewById<AppCompatButton>(R.id.fave_snacks_meal_name).text = mealName + " - " + mealCount

            // Assuming mealViewModel.state.value.meals is a list of meals with ingredients
            val mealWithIngredients = mealViewModel.state.value.meals
                .find { it.meal.name == mealName && formattedDates.contains(it.meal.timestamp) }

            if (mealWithIngredients != null && mealWithIngredients.ingredients.any {it.calories > 1} &&
                (mealWithIngredients.ingredients.any { it.fat100g != 0.0 } ||
                        mealWithIngredients.ingredients.any { it.proteins100g != 0.0 } ||
                        mealWithIngredients.ingredients.any { it.salt100g != 0.0 } ||
                        mealWithIngredients.ingredients.any { it.sugars100g != 0.0 })) {

                snackDetailButton.findViewById<AppCompatButton>(R.id.fave_snacks_meal_name).setOnClickListener {
                    onNutritionalInfoButtonClicked(
                        activity,
                        mealWithIngredients.ingredients.sumOf { ingredient -> ingredient.calories },
                        mealWithIngredients.meal.name,
                        mealWithIngredients.ingredients
                    )
                }
            } else {
                snackDetailButton.findViewById<AppCompatButton>(R.id.fave_snacks_meal_name).setOnClickListener {
                    AlertDialog.Builder(this.activity)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("No Nutritional Data present!")
                        .setPositiveButton("Alrighty") { _, _ ->
                        }.create().show()
                }
                snackDetailButton.findViewById<AppCompatButton>(R.id.fave_snacks_meal_name).background = resources.getDrawable(R.drawable.add_ingredient_button_greyed_out_left)
            }
            faveSnacksScrollView.addView(snackDetailButton)
        }
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


    private fun prepareChartData(viewModelMeal: MealViewModel, activity: MainScreenActivity, inflater: LayoutInflater) {
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
                    if (relevantMeals.isEmpty()) {
                        values.add(BarEntry(i.toFloat(), 0f))
                        continue
                    }
                    var totalCalsForDay = 0
                    relevantMeals.forEach { mealWithIngredients: MealWithIngredients -> totalCalsForDay += mealWithIngredients.ingredients.sumOf { ingredients -> ingredients.calories } }
                    relevantMeals.forEach { mealWithIngredients: MealWithIngredients -> mealsMappedToDays.add(Pair(formattedDates[i], mealWithIngredients))}

                    values.add(BarEntry(i.toFloat(), totalCalsForDay.toFloat()))

                    chart?.setOnChartValueSelectedListener(BarChartOnChartValueSelectedListener(
                        mealsMappedToDays, activity, formattedDates, inflater, viewModelMeal))
                }
                updateChart(values)

                prepareGeneralData(activity, viewModelMeal, formattedDates, inflater)
            }
        }
    }

    private fun updateChart(values: ArrayList<BarEntry>) {

        val set1: BarDataSet = BarDataSet(values, SET_LABEL)
        set1.color = ColorTemplate.rgb("#98FF98")
        val dataSets: ArrayList<IBarDataSet> = arrayListOf()
        dataSets.add(set1)
        val data: BarData = BarData(dataSets)

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
    private lateinit var inflater: LayoutInflater
    private lateinit var mealViewModel: MealViewModel


    constructor(
        mealsMappedToDays: ArrayList<Pair<String,MealWithIngredients>>,
        activity: MainScreenActivity,
        formattedDates: List<String>,
        inflater: LayoutInflater,
        mealViewModel: MealViewModel)
    {
        this.formattedDates = formattedDates
        this.mealsMappedToDays = mealsMappedToDays
        this.activity = activity
        this.inflater = inflater
        this.mealViewModel = mealViewModel

    }
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if ( e == null || e.y.toDouble() == 0.0 ) {
            onNothingSelected()
            return
        }
        val informationLayout = activity.findViewById<LinearLayout>(R.id.meal_clicked_information)
        informationLayout.removeAllViews()
        informationLayout.visibility = View.VISIBLE

        activity.findViewById<LinearLayout>(R.id.fave_snacks_last_meal_container).visibility = View.GONE


        mealsMappedToDays.filter { mealsMappedToDays -> mealsMappedToDays.first == formattedDates[e?.x?.toInt()!!]}.forEach { pair ->


            val mealDetailButton = inflater.inflate(R.layout.add_meal_button, null, false)

            val showIngredients = mealDetailButton.findViewById<AppCompatButton>(R.id.meal_ingredient_detail)
            val showNutritionalInfo = mealDetailButton.findViewById<AppCompatButton>(R.id.meal_nutritional_info)

            val deleteMeal = mealDetailButton.findViewById<AppCompatButton>(R.id.meal_ingredient_detail_remove)

            val nameContainer = mealDetailButton.findViewById<AppCompatTextView>(R.id.meal_barchat_name)
            val caloryContainer = mealDetailButton.findViewById<AppCompatTextView>(R.id.meal_barchat_calories)

            val totalCaloryForMeal = pair.second.ingredients.sumOf { ingredient -> ingredient.calories };

            showIngredients.setOnClickListener {
                onDetailButtonClicked(pair.second.ingredients)
            }

            if (
                pair.second.ingredients.any { it.fat100g != 0.0 } ||
                pair.second.ingredients.any { it.proteins100g != 0.0 } ||
                pair.second.ingredients.any { it.salt100g != 0.0 } ||
                pair.second.ingredients.any { it.sugars100g != 0.0 } )
            {
                showNutritionalInfo.setOnClickListener {
                    onNutritionalInfoButtonClicked(totalCaloryForMeal, pair.second.meal.name , pair.second.ingredients)
                }
            } else {
                showNutritionalInfo.setOnClickListener {
                    AlertDialog.Builder(this.activity)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("No Nutritional Data present!")
                        .setPositiveButton("Alrighty") {_, _ ->
                        }.create().show()
                }
                showNutritionalInfo.setBackgroundColor(ColorTemplate.rgb("#363636"))
            }



            deleteMeal.setOnClickListener {
                onRemoveButtonClicked(pair.second.meal, this.mealViewModel, informationLayout, mealDetailButton)
            }

            nameContainer.text = pair.second.meal.name
            caloryContainer.text = totalCaloryForMeal.toString() + " cals"

            informationLayout.addView(mealDetailButton)
        }
    }

    private fun onNutritionalInfoButtonClicked(calories: Int, mealName: String, ingredients: List<Ingredient>) {
        val intent = Intent(activity, MealNutritionActivity::class.java)
        intent.putParcelableArrayListExtra("ingredients", ArrayList(ingredients))
        intent.putExtra("mealCals", calories.toString())
        intent.putExtra("mealName", mealName)
        activity.startActivity(intent)
    }

    private fun onDetailButtonClicked(ingredients: List<Ingredient>) {
        val intent = Intent(activity, IngredientDetailActivity::class.java)
        intent.putParcelableArrayListExtra("ingredients", ArrayList(ingredients))
        activity.startActivity(intent)
    }

    private fun onRemoveButtonClicked(meal: Meal, mealViewModel: MealViewModel, view: LinearLayout, component: View) {
        AlertDialog.Builder(this.activity)
            .setIcon(com.kidrich.turbotrack.R.drawable.baseline_delete_forever_24)
            .setTitle("Are you sure you want to delete this Meal?")
            .setPositiveButton("Alrighty") {_, _ ->
                CoroutineScope(Dispatchers.Main).launch {
                    view.removeView(component)
                    mealViewModel.onEvent(MealEvent.DeleteMeal(meal))
                }
            }.create().show()
    }

    override fun onNothingSelected() {

        if(activity.findViewById<LinearLayout>(R.id.meal_clicked_information).visibility == View.GONE) {
            return
        }
        activity.findViewById<LinearLayout>(R.id.meal_clicked_information).visibility = View.GONE


        val relevantMeals = mealViewModel.state.value.meals.filter { meals ->
            formattedDates.contains(meals.meal.timestamp) && !meals.meal.isSnack
        }

        val relevantSnacks = mealViewModel.state.value.meals
            .filter { meals ->
                formattedDates.contains(meals.meal.timestamp) && meals.meal.isSnack
            }
            .groupBy { it.meal.name } // Group meals by name
            .map { (name, mealsList) -> name to mealsList.size } // Map each group to Pair(name, count)
            .sortedByDescending { it.second } // Sort by count in descending order


        activity.findViewById<LinearLayout>(R.id.fave_snacks_last_meal_container).visibility = View.VISIBLE

        val lastMealsScrollView = activity.findViewById<LinearLayout>(R.id.scroll_view_last_meals)
        val faveSnacksScrollView = activity.findViewById<LinearLayout>(R.id.scroll_view_fave_snacks)

        lastMealsScrollView.removeAllViews()
        faveSnacksScrollView.removeAllViews()

        for (mealWithIngredients in relevantMeals) {
            val mealDetailButton = inflater.inflate(R.layout.row_show_meal_name_and_edit, null, false)
            mealDetailButton.findViewById<AppCompatButton>(R.id.last_meals_meal_name).text = mealWithIngredients.meal.name

            if (
                mealWithIngredients.ingredients.any { it.fat100g != 0.0 } ||
                mealWithIngredients.ingredients.any { it.proteins100g != 0.0 } ||
                mealWithIngredients.ingredients.any { it.salt100g != 0.0 } ||
                mealWithIngredients.ingredients.any { it.sugars100g != 0.0 } )
            {
                mealDetailButton.findViewById<AppCompatButton>(R.id.last_meals_meal_name).setOnClickListener {
                    onNutritionalInfoButtonClicked( mealWithIngredients.ingredients.sumOf { ingredient -> ingredient.calories }, mealWithIngredients.meal.name , mealWithIngredients.ingredients)
                }
            } else {
                mealDetailButton.findViewById<AppCompatButton>(R.id.last_meals_meal_name).setOnClickListener {
                    AlertDialog.Builder(this.activity)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("No Nutritional Data present!")
                        .setPositiveButton("Alrighty") {_, _ ->
                        }.create().show()
                }
                mealDetailButton.findViewById<AppCompatButton>(R.id.last_meals_meal_name).background = activity.resources.getDrawable(R.drawable.add_ingredient_button_greyed_out_right)
            }
            lastMealsScrollView.addView(mealDetailButton)
        }

        for ((mealName, mealCount) in relevantSnacks) {
            val snackDetailButton = inflater.inflate(R.layout.row_show_snack_name_and_edit, null, false)
            snackDetailButton.findViewById<AppCompatButton>(R.id.fave_snacks_meal_name).text = mealName + " - " + mealCount

            // Assuming mealViewModel.state.value.meals is a list of meals with ingredients
            val mealWithIngredients = mealViewModel.state.value.meals
                .find { it.meal.name == mealName && formattedDates.contains(it.meal.timestamp) }

            if (mealWithIngredients != null &&
                (mealWithIngredients.ingredients.any { it.fat100g != 0.0 } ||
                        mealWithIngredients.ingredients.any { it.proteins100g != 0.0 } ||
                        mealWithIngredients.ingredients.any { it.salt100g != 0.0 } ||
                        mealWithIngredients.ingredients.any { it.sugars100g != 0.0 })) {

                snackDetailButton.findViewById<AppCompatButton>(R.id.fave_snacks_meal_name).setOnClickListener {
                    onNutritionalInfoButtonClicked(
                        mealWithIngredients.ingredients.sumOf { ingredient -> ingredient.calories },
                        mealWithIngredients.meal.name,
                        mealWithIngredients.ingredients
                    )
                }
            } else {
                snackDetailButton.findViewById<AppCompatButton>(R.id.fave_snacks_meal_name).setOnClickListener {
                    AlertDialog.Builder(this.activity)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("No Nutritional Data present!")
                        .setPositiveButton("Alrighty") { _, _ ->
                        }.create().show()
                }
                snackDetailButton.findViewById<AppCompatButton>(R.id.fave_snacks_meal_name).background = activity.resources.getDrawable(R.drawable.add_ingredient_button_greyed_out_left)
            }
            faveSnacksScrollView.addView(snackDetailButton)
        }
    }
}