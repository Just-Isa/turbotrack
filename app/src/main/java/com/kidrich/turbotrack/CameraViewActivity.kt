package com.kidrich.turbotrack


import OpenFoodFactsApiTask
import ProductResponse
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.kidrich.turbotrack.databinding.ActivityCameraviewBinding


class CameraViewActivity : AppCompatActivity(), ApiTaskCallback {

    private lateinit var binding: ActivityCameraviewBinding
    private var previewView: PreviewView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraviewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.cameraTakePictureButton.setOnClickListener {
            scanCode()
        }


    }

    private fun scanCode() {
        val integrator: IntentIntegrator = IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct::class.java)
        integrator.setOrientationLocked(true)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scanning BarCode")
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val apiTask = OpenFoodFactsApiTask(this)
                apiTask.execute(result.contents)
            } else {
                showAlertDialog("No qr code found!")
            }
        }
    }

    private fun showAlertDialog(text: String) {
        android.app.AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(text)
            .setPositiveButton("Alrighty") {_, _ ->
                //pass
            }.create().show()
    }

    override fun onApiTaskComplete(result: ProductResponse?) {
        // Handle the API result in your activity
        if (result != null) {
            Log.d("YourActivity", "API response: $result")
            val intent = Intent()
            Log.d("testHode", result.product.nutriments.energyKcal100g.toString())
            Log.d("testHode", result.product.product_name)
            intent.putExtra("kcalperhundred", result.product.nutriments.energyKcal100g.toString())
            intent.putExtra("genericname", result.product.product_name)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            Log.e("YourActivity", "API call failed")
        }
    }

    override fun onApiTaskError() {
        Log.e("YourActivity", "API call failed")
    }

}