package com.example.panicbuttonrtdb.prensentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.panicbuttonrtdb.R
import com.example.panicbuttonrtdb.data.MonitorRecord
import com.example.panicbuttonrtdb.viewmodel.ViewModel
import kotlinx.coroutines.delay
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.panicbuttonrtdb.utils.openGoogleMaps

@Composable
fun DetailRekapItem(
    modifier: Modifier = Modifier,
    record: MonitorRecord,
    viewModel: ViewModel

) {

    val context = LocalContext.current

    LaunchedEffect(record) {
        while (true) {
            viewModel.detailRekap(record.houseNumber)
            delay(5000)
        }
    }

    Card(
        modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 8.dp), // Beri sedikit jarak antar card
        colors = if (record.status == "Selesai") CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.background_card)
        ) else CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        // Gunakan Column sebagai container utama untuk menata elemen secara vertikal
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Jarak antar elemen vertikal
        ) {
            // --- BARIS ATAS: Info Utama ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.houseNumber,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.font2)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Box untuk Prioritas
                Box(
                    modifier = Modifier
                        .background(
                            color = when (record.priority) {
                                "Darurat" -> colorResource(id = R.color.darurat)
                                "Penting" -> colorResource(id = R.color.penting)
                                else -> colorResource(id = R.color.biasa)
                            },
                            shape = RoundedCornerShape(6.dp)
                        )
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        text = record.priority,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(1f)) // Spacer ini akan mendorong waktu ke ujung kanan
                Text(
                    text = record.time,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(id = R.color.primary)
                )
            }

            // --- BAGIAN TENGAH: Pesan ---
            if (record.message.isNotBlank()) {
                Text(
                    text = record.message,
                    fontSize = 12.sp,
                    color = colorResource(id = R.color.font3),
                    style = TextStyle(lineHeight = 20.sp),
                    overflow = TextOverflow.Ellipsis
                )
            }

            // --- BARIS LOKASI ---
            // Hanya tampilkan baris ini jika ada data lokasi
            if (record.latitude != 0.0 || record.longitude != 0.0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Lokasi: ${String.format("%.4f", record.latitude)}, ${String.format("%.4f", record.longitude)}",
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.font3)
                    )
                    Row(
                        modifier = Modifier.clickable { // <-- Tambahkan clickable pada Row
                            openGoogleMaps(context, record.latitude, record.longitude)
                        },
                        verticalAlignment = Alignment.CenterVertically, // <-- Agar teks & ikon sejajar
                        horizontalArrangement = Arrangement.spacedBy(4.dp) // <-- Memberi jarak antar teks & ikon
                    ) {
                        // Teks yang bisa diklik
                        Text(
                            text = "Lihat lokasi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorResource(id = R.color.primary) // Warna disamakan agar terlihat seperti link
                        )
                        // Ikon yang menyertai teks
                        Icon(
                            modifier = Modifier.size(16.dp), // Ukuran ikon bisa disesuaikan
                            painter = painterResource(id = R.drawable.ic_maps),
                            contentDescription = "Ikon Peta",
                            tint = colorResource(id = R.color.primary)
                        )
                    }
                    }
                }
            }


            // --- BAGIAN BAWAH: Tombol Konfirmasi ---
            if (record.id.isNotEmpty()) {
                ConfirmationButton(
                    viewModel = viewModel,
                    record = record,
                    onConfirm = { viewModel.updateStatus(record.id) }
                )
            } else {
                Log.e("DetailRekapItem", "Record ID is empty, cannot update status.")
            }
        }
    }

