package com.kidrich.turbotrack

import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kidrich.turbotrack.databinding.ActivityMainscreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.properties.Delegates

class MainScreenActivity: AppCompatActivity() {


    private var totalCal: Int by Delegates.observable(0) { _, _, newValue ->
        // Update the TextView when the observed value changes
        binding.texttest.text = "Value: $newValue"
    }

    private val REQUEST_CAMERA_PERMISSION = 1001;
    private val REQUEST_IMAGE_CAPTURE = 1

    private val MAX_X_VALUE = 7
    private val SET_LABEL = "Daily Calories"

    private var photoFile: File? = null


    private lateinit var binding: ActivityMainscreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        val viewModelIngredient by viewModels<IngredientViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return IngredientViewModel(db.ingredientDao) as T
                    }
                }
            }
        )

        // Setting click listeners for the buttons
        setClickListeners(viewModelMeal, viewModelIngredient)

        if (savedInstanceState == null) {
            val fragment = VerticalBarChartFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

    private fun setClickListeners(viewModelMeal: MealViewModel,
                                  viewModelIngredient: IngredientViewModel
    ) {
        binding.addMealButton.setOnClickListener {
            d("MainScreen", "Add Meal");
            binding.texttest.text = "mEALung";

            startActivity(Intent(this, AddMealActivity::class.java))

        }

        binding.addSnackButton.setOnClickListener {
            d("MainScreen", "Add Day and ingredient");
            CoroutineScope(Dispatchers.Main).launch {
                try {
                } catch(e: Exception) {
                    d("savestufff", e.toString())
                }
            }
        }

        binding.entercalories.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch {
                try {

                } catch (e: Exception) {


                }
            }
        }
    }



}
