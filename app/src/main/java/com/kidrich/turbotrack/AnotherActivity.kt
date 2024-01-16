package com.kidrich.turbotrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kidrich.turbotrack.databinding.ActivityAnotherBinding

class AnotherActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAnotherBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAnotherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storage = getSharedPreferences("storage", Context.MODE_PRIVATE)

        binding.anotherActivityName.text = storage.getString("loggedInUser", "Not logged in!")

        //get something from storage
        binding.button2.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}