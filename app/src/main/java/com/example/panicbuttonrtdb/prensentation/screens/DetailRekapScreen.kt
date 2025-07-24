package com.example.panicbuttonrtdb.prensentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.panicbuttonrtdb.R
import com.example.panicbuttonrtdb.data.User
import com.example.panicbuttonrtdb.prensentation.components.DetailRekapItem
import com.example.panicbuttonrtdb.prensentation.components.UserInformationForAdmin
import com.example.panicbuttonrtdb.viewmodel.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun DetailRekapScreen(
    modifier: Modifier = Modifier,
    viewModel: ViewModel,
    navController: NavController,
    houseNumber: String
) {
    // --- Blok Inisialisasi Data ---
    val databaseRef = FirebaseDatabase.getInstance().getReference("users")
    var user by remember { mutableStateOf<User?>(null) }
    val emptyProfile = R.drawable.ic_empty_profile
    val emptyCover = R.drawable.empty_image
    val record by viewModel.monitorData.observeAsState(emptyList())
    val unit = record.filter { it.houseNumber == houseNumber }
    val profileImageUrl = if (user?.imageProfile.isNullOrEmpty()) emptyProfile else user?.imageProfile
    val coverImageUrl = if (user?.coverImage.isNullOrEmpty()) emptyCover else user?.coverImage

    // --- BARIS INI DIPINDAHKAN KE SINI DARI DALAM LAZYCOLUMN ---
    val userName = record.firstOrNull()?.name ?: "Nama Pengguna"

    LaunchedEffect(houseNumber) {
        databaseRef.orderByChild("houseNumber").equalTo(houseNumber)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        user = snapshot.children.firstNotNullOfOrNull { it.getValue(User::class.java) }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        viewModel.detailRekap(houseNumber)
    }
    // --- Akhir Blok Inisialisasi Data ---

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.primary))
    ) {
        // ITEM 1: Bagian Header (Cover, Profil, Nama)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                // Cover Image
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    painter = rememberImagePainter(data = coverImageUrl),
                    contentDescription = "cover_image",
                    contentScale = ContentScale.Crop
                )

                // Tombol Back
                IconButton(
                    modifier = Modifier.padding(top = 40.dp, start = 24.dp),
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Kembali",
                        tint = Color.White
                    )
                }

                // Card untuk Nama & Nomor Rumah
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 100.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            // variabel userName sekarang dikenali di sini
                            Text(text = userName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorResource(id = R.color.font))
                            Text(text = "Nomor Rumah: $houseNumber", fontSize = 12.sp, color = colorResource(id = R.color.font2))
                        }
                    }
                }

                // Foto Profil
                Image(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp)
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(4.dp, colorResource(id = R.color.primary), CircleShape),
                    painter = rememberImagePainter(data = profileImageUrl),
                    contentDescription = "profile_image",
                    contentScale = ContentScale.Crop
                )
            }
        }

        // ITEM 2: Judul "Keterangan"
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "Keterangan",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ITEM 3: Komponen UserInformationForAdmin
        item {
            UserInformationForAdmin(
                viewModel = viewModel,
                houseNumber = houseNumber
            )
        }

        // ITEM 4: Judul "Detail Rekap"
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "Detail Rekap",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ITEM 5: Latar Belakang Putih untuk List
        item {
            Box(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    Color.White,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .padding(top = 24.dp)
            )
        }

        // ITEM 6: Daftar Detail Rekap
        items(unit) { log ->
            Box(modifier = Modifier.background(Color.White)){
                DetailRekapItem(
                    record = log,
                    viewModel = viewModel,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}