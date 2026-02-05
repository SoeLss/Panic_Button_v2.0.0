package com.example.panicbuttonrtdb

import android.app.Application
import com.google.firebase.FirebaseApp

class PanicButtonApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inisialisasi Firebase secara manual saat aplikasi pertama kali dibuat
        // Ini memastikan Firebase siap digunakan di semua proses, termasuk background worker.
        FirebaseApp.initializeApp(this)
    }
}