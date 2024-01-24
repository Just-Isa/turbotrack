package com.kidrich.turbotrack

import ProductResponse


interface ApiTaskCallback {
    fun onApiTaskComplete(result: ProductResponse?)
    fun onApiTaskError()
}