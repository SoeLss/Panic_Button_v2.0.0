package com.example.panicbuttonrtdb.widget // Disarankan menggunakan package terpisah untuk widget

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import com.example.panicbuttonrtdb.R
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

// 1. Kelas utama Widget Anda
class PanicButtonWidget : GlanceAppWidget() {

    // --- PERBAIKAN DI SINI ---
    // Hapus fungsi Content() dan pindahkan logikanya ke provideGlance()
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            PanicButtonWidgetContent()
        }
    }
}

// 2. Tampilan (UI) Widget Anda (Tidak ada perubahan di sini)
@Composable
fun PanicButtonWidgetContent() {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            provider = ImageProvider(R.drawable.ic_panic_button_widget),
            contentDescription = "Panic Button",
            modifier = GlanceModifier
                .fillMaxSize()
                .clickable(actionRunCallback<PanicClickAction>())
        )
    }
}

// 3. Logika yang dijalankan saat widget ditekan (Tidak ada perubahan di sini)
class PanicClickAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("PanicWidget", "Aksi dari widget Glance dijalankan!")
        Toast.makeText(context, "Mengirim sinyal darurat...", Toast.LENGTH_SHORT).show()

        // Buat permintaan kerja satu kali
        val sendPanicWorkRequest = OneTimeWorkRequestBuilder<SendPanicDataWorker>().build()

        // Jalankan permintaan kerja menggunakan WorkManager
        WorkManager.getInstance(context).enqueue(sendPanicWorkRequest)
    }
}

// 4. Receiver untuk Widget Glance Anda (Tidak ada perubahan di sini)
class PanicButtonWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PanicButtonWidget()
}