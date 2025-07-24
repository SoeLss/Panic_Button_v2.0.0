package com.example.panicbuttonrtdb.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

/**
 * Fungsi untuk mendapatkan lokasi pengguna saat ini menggunakan FusedLocationProviderClient.
 *
 * @param context Context dari aplikasi.
 * @param onLocationFetched Callback yang akan dipanggil dengan hasil latitude dan longitude.
 * Akan mengembalikan (0.0, 0.0) jika gagal atau tidak ada izin.
 */
fun getCurrentLocation(context: Context, onLocationFetched: (lat: Double, lon: Double) -> Unit) {
    // Membuat klien penyedia lokasi
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Memeriksa apakah izin lokasi sudah diberikan. Ini adalah lapisan keamanan kedua.
    // Pemeriksaan utama tetap harus dilakukan di Composable sebelum memanggil fungsi ini.
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(context, "Izin lokasi tidak diberikan.", Toast.LENGTH_SHORT).show()
        onLocationFetched(0.0, 0.0) // Mengirimkan nilai default jika tidak ada izin
        return
    }

    // Mengambil lokasi saat ini dengan akurasi tinggi
    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        .addOnSuccessListener { location ->
            if (location != null) {
                // Lokasi berhasil didapatkan, kirim hasilnya melalui callback
                Log.d("LocationUtils", "Lokasi berhasil didapat: Lat: ${location.latitude}, Lon: ${location.longitude}")
                onLocationFetched(location.latitude, location.longitude)
            } else {
                // Lokasi null, kemungkinan GPS mati
                Log.w("LocationUtils", "Gagal mendapatkan lokasi, hasilnya null.")
                Toast.makeText(context, "Gagal mendapatkan lokasi. Pastikan GPS aktif.", Toast.LENGTH_SHORT).show()
                onLocationFetched(0.0, 0.0)
            }
        }
        .addOnFailureListener { e ->
            // Terjadi error saat mencoba mendapatkan lokasi
            Log.e("LocationUtils", "Error mendapatkan lokasi", e)
            Toast.makeText(context, "Terjadi error saat mengambil lokasi.", Toast.LENGTH_SHORT).show()
            onLocationFetched(0.0, 0.0)
        }
}

