package com.mahdid.o.taha.onlineshoppingsystem.model

import android.graphics.Bitmap
import com.google.type.LatLng

data class products(
    val name: String?,
    val description: String?,
    val image: Bitmap,
    val price: Double?,
    val rate: Float,
    val location: LatLng?
)
