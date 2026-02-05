package com.example.panicbuttonrtdb.prensentation.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.panicbuttonrtdb.notification.openNotificationSettings
import com.example.panicbuttonrtdb.prensentation.components.UserInformation
import com.example.panicbuttonrtdb.viewmodel.ViewModel


@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    context: Context,
    navController: NavController,
    viewModel: ViewModel
) {

    // <-- Ambil data dari ViewModel, bukan dari SharedPreferences langsung -->
    val userName = viewModel.currentUserName
    val nomorRumah = viewModel.currentUserHouseNumber

    // <-- Ambil data User dari LiveData di ViewModel -->
    val user by viewModel.userProfileData.observeAsState(null)

    val emptyProfile = R.drawable.ic_empty_profile
    val emptyCover = R.drawable.empty_image

    // Logika untuk menampilkan gambar tetap sama
    val profileImageUrl = if (user?.imageProfile.isNullOrEmpty()) emptyProfile else user?.imageProfile
    val coverImageUrl = if (user?.coverImage.isNullOrEmpty()) emptyCover else user?.coverImage

    // <-- Pemanggilan uploadImage diperbarui, tidak perlu 'context' dan 'houseNumber' -->
    val profileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.uploadImage(it, "profileImage")
        }
    }
    val coverLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.uploadImage(it, "coverImage")
        }
    }

    // <-- Panggil fungsi ViewModel untuk mengambil data saat layar dibuka -->
    LaunchedEffect(Unit) {
        viewModel.fetchUserProfileData()
    }

    Box(
        modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.primary))
    ) {
        Box(
            modifier
                .height(180.dp)
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth(),
                painter = rememberImagePainter(data = coverImageUrl),
                contentDescription = "cover_image",
                contentScale = ContentScale.Crop
            )
            Row(
                modifier
                    .padding(start = 24.dp, end = 24.dp, top = 40.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    onClick = {
                        navController.popBackStack() // Lebih baik menggunakan popBackStack untuk kembali
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colorResource(id = R.color.background_button),
                        contentColor = colorResource(id = R.color.primary)
                    )
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = null
                    )
                }
                IconButton(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    onClick = { coverLauncher.launch("image/*") },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colorResource(id = R.color.background_button),
                        contentColor = colorResource(id = R.color.primary)
                    )
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = null
                    )
                }
            }
        }
        Box(
            modifier
                .wrapContentSize()
                .padding(top = 160.dp)
        ){
            Card(
                modifier
                    .padding(top = 26.dp)
                    .height(54.dp)
                    .fillMaxWidth()
                    .padding(start = 54.dp, end = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.elevatedCardElevation(4.dp)
            ) {
                Column(
                    modifier
                        .padding(start = 60.dp, top = 4.dp, end = 6.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = userName.ifEmpty { "Nama Pengguna" },
                        fontSize = 18.sp,
                        color = colorResource(id = R.color.font),
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nomor Rumah anda",
                            fontSize = 12.sp
                        )
                        Text(
                            text = nomorRumah.ifEmpty { "-" },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.font2)
                        )
                    }
                }
            }
            Box(
                modifier
                    .wrapContentSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clip(CircleShape)
                        .size(80.dp)
                        .background(color = Color.White)
                        .border(
                            width = 4.dp,
                            color = colorResource(id = R.color.primary),
                            shape = RoundedCornerShape(100.dp)
                        ),
                    painter = rememberImagePainter(data = profileImageUrl),
                    contentDescription = "ic_empty_profile",
                    contentScale = ContentScale.Crop
                )
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { profileLauncher.launch("image/*") },
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "ic_edit",
                    tint = colorResource(id = R.color.primary)
                )
            }
        }
        Column(
            modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(top = 260.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(start = 24.dp),
                    text = "Keterangan",
                    fontSize = 14.sp,
                    color = Color.White
                )
                // UserInformation juga sebaiknya menggunakan houseNumber dari ViewModel
                UserInformation(viewModel = viewModel)

                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Pengaturan",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        onClick = { openNotificationSettings(context) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.background_button_userInformationScreen)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Row(
                            modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(id = R.drawable.ic_notification),
                                contentDescription = "ic_notification",
                                tint = Color.White
                            )
                            Text(
                                text = "Notifikasi",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        onClick = { navController.navigate("help") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.background_button_userInformationScreen)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Row(
                            modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(id = R.drawable.ic_help),
                                contentDescription = "ic_help",
                                tint = Color.White
                            )
                            Text(
                                text = "Bantuan",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}