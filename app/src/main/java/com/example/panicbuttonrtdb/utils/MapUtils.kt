package com.example.panicbuttonrtdb.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun openGoogleMaps(context: Context, latitude: Double, longitude: Double) {
    // Jangan buka map jika koordinat tidak valid
    if (latitude == 0.0 && longitude == 0.0) {
        return
    }

    val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(Lokasi Darurat)")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    }
}