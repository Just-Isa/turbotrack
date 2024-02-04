package com.kidrich.turbotrack

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kidrich.turbotrack.databinding.ActivityMainscreenBinding

class MainScreenActivity: AppCompatActivity() {

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
