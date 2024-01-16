package com.kidrich.turbotrack

import android.content.Context
import android.database.Observable
import android.os.Bundle
import android.util.Log.d
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.kidrich.turbotrack.databinding.ActivityMainscreenBinding
import java.io.File
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

        val storage = getSharedPreferences("storage", Context.MODE_PRIVATE);

        binding.addMealButton.setOnClickListener {
            d("MainScreen", "Add Meal");
            binding.texttest.text = "mEALung";

        }

        binding.addSnackButton.setOnClickListener {
            d("MainScreen", "Add Snack");
            binding.texttest.text = "sNACCung";
        }

        binding.entercalories.setOnClickListener {
            if (binding.hiddenCalorieInput.visibility == View.VISIBLE) {
                totalCal = totalCal + binding.hiddenCalorieInput.text.toString().toInt();
                binding.hiddenCalorieInput.visibility = View.GONE;
            } else {
                binding.hiddenCalorieInput.visibility = View.VISIBLE;
            }
        }
    }
}
