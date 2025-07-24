package com.example.panicbuttonrtdb

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.core.content.ContextCompat
import com.example.panicbuttonrtdb.navigation.MainApp
import com.example.panicbuttonrtdb.notification.createNotificationChannel
import com.example.panicbuttonrtdb.notification.sendNotification
import com.example.panicbuttonrtdb.ui.theme.PanicButtonRtdbTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
                //menggunakan ActivityResultContracts utk meminta izin post notifikasi
            ) {}

            if (ContextCompat.checkSelfPermission(
                    this, "android.permission.POST_NOTIFICATIONS"
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch("android.permission.POST_NOTIFICATIONS")
            }
        }

        createNotificationChannel(this)
        setContent {
            val lockFont = Density(density = LocalDensity.current.density, fontScale = 1f)
            CompositionLocalProvider(LocalDensity provides lockFont) {
//                PanicButtonRtdbTheme {
                    MainApp()
//                }
            }
        }
    }
}


