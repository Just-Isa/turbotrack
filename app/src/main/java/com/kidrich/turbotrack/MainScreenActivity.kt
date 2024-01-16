package com.kidrich.turbotrack

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kidrich.turbotrack.databinding.ActivityMainscreenBinding
import android.util.Log.d

class MainScreenActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainscreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainscreenBinding.inflate(layoutInflater);
        setContentView(binding.root);

        val storage = getSharedPreferences("storage", Context.MODE_PRIVATE);

        binding.addMealButton.setOnClickListener {
            d("MainScreen", "Add Meal");
        }

        binding.addSnackButton.setOnClickListener {
            d("MainScreen", "Add Snack");
        }
    }


}