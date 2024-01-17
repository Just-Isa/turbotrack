package com.kidrich.turbotrack

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
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date
import kotlin.properties.Delegates

class MainScreenActivity: AppCompatActivity() {


    private var totalCal: Int by Delegates.observable(0) { _, _, newValue ->
        // Update the TextView when the observed value changes
        binding.texttest.text = "Value: $newValue"
    }

    private val REQUEST_CAMERA_PERMISSION = 1001;
    private val REQUEST_IMAGE_CAPTURE = 1

    private var photoFile: File? = null


    private lateinit var binding: ActivityMainscreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainscreenBinding.inflate(layoutInflater);
        setContentView(binding.root);

        val db  = MealDatabase.getInstance(applicationContext);

        val viewModel by viewModels<MealViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MealViewModel(db.dao) as T
                    }
                }
            }
        )

        binding.addMealButton.setOnClickListener {
            d("MainScreen", "Add Meal");
            binding.texttest.text = "mEALung";
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    withContext(Dispatchers.IO) {
                        viewModel.onEvent(MealEvent.SetCalories(200))
                        viewModel.onEvent(MealEvent.SetTimestamp(Date()))
                        viewModel.onEvent(MealEvent.SetIsSnack(false))
                        viewModel.onEvent(MealEvent.SaveMeal)
                    }
                } catch(e: Exception) {
                    d("savestufff", e.toString())
                }
            }

        }

        binding.addSnackButton.setOnClickListener {
            d("MainScreen", "Add Snack");
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    viewModel.onEvent(MealEvent.DateType(DateType.TODAY))
                    viewModel.onEvent(MealEvent.SortType(SortType.ALL))
                    viewModel.state.collect { state ->
                        val result = state.meals
                        binding.texttest.text = result.toString()
                    }
                } catch(e: Exception) {
                    d("savestufff", e.toString())
                }
            }

        }

        binding.entercalories.setOnClickListener {
        }
    }
}
