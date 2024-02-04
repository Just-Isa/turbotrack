package com.kidrich.turbotrack

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kidrich.turbotrack.databinding.ActivityMainscreenBinding
import java.io.File

class MainScreenActivity: AppCompatActivity() {



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

        setClickListeners()

        val fragment = VerticalBarChartFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setClickListeners(
    ) {
        binding.addMealButton.setOnClickListener {
            startActivity(Intent(this, AddMealActivity::class.java))
        }
    }
}
