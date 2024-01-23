package com.kidrich.turbotrack

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCapture.OutputFileResults
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors


class CameraViewActivity : AppCompatActivity() {

    var capture: AppCompatButton? = null
    private var previewView: PreviewView? = null
    var cameraFacing = CameraSelector.LENS_FACING_BACK
    private val activityResultLauncher = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) {
            startCamera(cameraFacing)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.kidrich.turbotrack.R.layout.activity_cameraview)
        previewView = findViewById<PreviewView>(com.kidrich.turbotrack.R.id.camera_preview_view)
        capture = findViewById<AppCompatButton>(com.kidrich.turbotrack.R.id.camera_take_picture_button)
        if (ContextCompat.checkSelfPermission(
                this@CameraViewActivity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activityResultLauncher.launch(Manifest.permission.CAMERA)
        } else {
            startCamera(cameraFacing)
        }
    }

    fun startCamera(cameraFacing: Int) {
        val aspectRatio = aspectRatio(previewView!!.width, previewView!!.height)
        val listenableFuture = ProcessCameraProvider.getInstance(this)
        listenableFuture.addListener({
            try {
                val cameraProvider =
                    listenableFuture.get() as ProcessCameraProvider
                val preview =
                    Preview.Builder().setTargetAspectRatio(aspectRatio).build()
                val imageCapture =
                    ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(windowManager.defaultDisplay.rotation).build()
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(cameraFacing).build()
                cameraProvider.unbindAll()
                val camera =
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                capture!!.setOnClickListener {
                    if (ContextCompat.checkSelfPermission(
                            this@CameraViewActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                    takePicture(imageCapture)
                }
                preview.setSurfaceProvider(previewView!!.surfaceProvider)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    fun takePicture(imageCapture: ImageCapture) {
        val file = File(getExternalFilesDir(null), System.currentTimeMillis().toString() + ".jpg")
        val outputFileOptions = OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(
            outputFileOptions,
            Executors.newCachedThreadPool(),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: OutputFileResults) {
                    runOnUiThread {
                        Toast.makeText(
                            this@CameraViewActivity,
                            "Image saved at: " + file.path,
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent()
                        intent.putExtra("imageFilePath", file.path)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    startCamera(cameraFacing)
                }

                override fun onError(exception: ImageCaptureException) {
                    runOnUiThread {
                        Toast.makeText(
                            this@CameraViewActivity,
                            "Failed to save: " + exception.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    startCamera(cameraFacing)
                }
            })
    }


    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = Math.max(width, height).toDouble() / Math.min(width, height)
        return if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            AspectRatio.RATIO_4_3
        } else AspectRatio.RATIO_16_9
    }
}