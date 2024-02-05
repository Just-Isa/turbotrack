package com.kidrich.turbotrack

import android.view.View
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class CaptureAct : CaptureActivity() {
    override fun initializeContent(): DecoratedBarcodeView {
        setContentView(R.layout.activity_capture)
        return (findViewById<View>(R.id.zxing_view) as DecoratedBarcodeView)
    }
}