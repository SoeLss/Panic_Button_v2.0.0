package com.example.panicbuttonrtdb.widget

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CountDownLatch

class SendPanicDataWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Log.d("PanicWorker", "WorkManager is running...")
        val context = applicationContext

        // Ambil data sesi dari SharedPreferences
        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)

        if (!isLoggedIn) {
            Log.d("PanicWorker", "User not logged in. Work cancelled.")
            return Result.failure()
        }

        val perumahanId = sharedPref.getString("perumahan_id", null)
        val userName = sharedPref.getString("user_name", "Unknown")
        val houseNumber = sharedPref.getString("house_number", "Unknown")

        if (perumahanId == null) {
            Log.d("PanicWorker", "Perumahan ID not found. Work cancelled.")
            return Result.failure()
        }

        // Gunakan CountDownLatch untuk menunggu hasil dari Firebase
        val latch = CountDownLatch(1)
        var wasSuccessful = false

        val monitorPath = FirebaseDatabase.getInstance().getReference("perumahan/$perumahanId/monitor")
        val timestamp = SimpleDateFormat("yyyy-MM-dd 'waktu' HH:mm", Locale.getDefault()).format(Date())

        val report = mapOf(
            "name" to userName,
            "houseNumber" to houseNumber,
            "message" to "Laporan Darurat dari Widget Home Screen",
            "priority" to "Darurat",
            "status" to "Proses",
            "time" to timestamp,
            "latitude" to 0.0,
            "longitude" to 0.0
        )

        monitorPath.push().setValue(report)
            .addOnSuccessListener {
                Log.d("PanicWorker", "Data successfully sent to Firebase.")
                val buzzerPath = FirebaseDatabase.getInstance().getReference("perumahan/$perumahanId/buzzers/main/state")
                buzzerPath.setValue("on")
                wasSuccessful = true
                latch.countDown() // Beri tahu bahwa proses selesai
            }
            .addOnFailureListener {
                Log.e("PanicWorker", "Failed to send data.", it)
                wasSuccessful = false
                latch.countDown() // Beri tahu bahwa proses selesai
            }

        // Tunggu hingga callback dari Firebase selesai
        try {
            latch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return if (wasSuccessful) Result.success() else Result.retry()
    }
}