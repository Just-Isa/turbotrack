package com.example.turbotrack

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.turbotrack.databinding.ActivityMainBinding
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log.d
import androidx.core.content.FileProvider
import java.io.File
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val REQUEST_CAMERA_PERMISSION = 1001
    private val REQUEST_IMAGE_CAPTURE = 1
    private var photoFile: File? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)

        val storage = getSharedPreferences("storage", Context.MODE_PRIVATE)

        binding.cameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )

            } else {
                dispatchTakePictureIntent()
            }
        }

        binding.loginButton.setOnClickListener {
            if (!binding.editUserName.text.isNullOrBlank() && !binding.editPassword.text.isNullOrBlank()) {
                storage.edit().apply {
                    putString("loggedInUser", binding.editUserName.text.toString())
                }.apply()
                startActivity(Intent(this, AnotherActivity::class.java))
            } else {
                showAlertDialog("Make sure to enter both Username and Password!");
            }
        }
    }

    // Event for taking the picture
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            takePictureIntent ->
            try {
                var photoFile = createImageFile()
                photoFile?.also { file ->
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.neighborhoodtalk.provider",
                        file
                    )
                    try {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    } catch (ex: Throwable) {
                        d("Camera Error!", ex.stackTraceToString() )
                    }
                }
            } catch (ex: Throwable) {
                d("Camera Error!", ex.stackTraceToString() )
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}",
            ".jpg",
            storageDir
                ).apply {
                    // Save a file path for use with ACTION_VIEW intents
                    photoFile = this
        }
    }

    // Overridden method of what gets called when the camera persmission is being asked for
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                showAlertDialog("Need Camera Permission!");
            }
        }
    }

    // Overridden method of what happens AFTER an activity has finished (in this case the
    // takePictureIntent which calls the startActivityForResult method
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // The photo was taken and saved
            val imageBitmap = BitmapFactory.decodeFile(photoFile?.absolutePath)
            // Do something with the imageBitmap (e.g., display it in an ImageView)
            binding.imageView.setImageBitmap(imageBitmap);
        }
    }

    private fun showAlertDialog(text: String) {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(text)
            .setPositiveButton("Ok") {_, _ ->
                //pass
            }.create().show()
    }
}