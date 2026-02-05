package com.example.panicbuttonrtdb.prensentation.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.panicbuttonrtdb.R
import com.example.panicbuttonrtdb.viewmodel.ViewModel

@Composable
fun UserInformation(
    modifier: Modifier = Modifier,
    viewModel: ViewModel
) {
    // <-- Ambil data user dari ViewModel -->
    val userData by viewModel.userData.observeAsState(mapOf())

    // <-- State untuk TextField, diupdate saat userData berubah -->
    var phoneNumber by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    // <-- LaunchedEffect ini akan mengupdate state TextField saat data dari ViewModel datang -->
    LaunchedEffect(userData) {
        phoneNumber = userData["phoneNumber"] ?: ""
        note = userData["note"] ?: ""
    }

    // <-- Ambil data user saat composable pertama kali ditampilkan -->
    LaunchedEffect(Unit) {
        // Asumsi houseNumber diambil dari sesi di dalam ViewModel
        viewModel.fetchUserData(viewModel.currentUserHouseNumber)
    }

    Surface(
        modifier
            .padding(horizontal = 24.dp)
            .wrapContentSize(),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_phonenumber),
                            contentDescription = "ic_phonenumber",
                            tint = colorResource(id = R.color.font2)
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Masukan No Hp Anda",
                            fontSize = 13.sp
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        cursorColor = colorResource(id = R.color.font),
                        unfocusedIndicatorColor = Color.White,
                        focusedIndicatorColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                )
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = colorResource(id = R.color.font3),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    value = note,
                    onValueChange = {note = it},
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_note),
                            contentDescription = "ic_note",
                            tint = colorResource(id = R.color.font2)
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Masukan keterangan tentang rumah anda",
                            fontSize = 13.sp
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        cursorColor = colorResource(id = R.color.font),
                        unfocusedIndicatorColor = Color.White,
                        focusedIndicatorColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                )
            }
        }
    }

    Button(
        onClick = {
            // <-- PERBAIKAN: Hapus parameter 'houseNumber' -->
            viewModel.savePhoneNumberAndNote(phoneNumber, note)
        },
        contentPadding = PaddingValues(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.background_button_userInformationScreen)
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(start = 24.dp)
    ) {
        Text(
            text = "Simpan",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )
    }
}


@Composable
fun UserInformationForAdmin(
    modifier: Modifier = Modifier,
    viewModel: ViewModel,
    houseNumber: String
) {
    val userData by viewModel.userData.observeAsState(mapOf())

    // <-- PERBAIKAN: Jangan gunakan 'remember', agar nilai selalu update dari 'userData' -->
    val phoneNumber = userData["phoneNumber"] ?: ""
    val note = userData["note"] ?: ""

    // <-- PERBAIKAN: Hapus loop 'while(true)' agar tidak fetch berulang-ulang -->
    LaunchedEffect(houseNumber) {
        viewModel.fetchUserData(houseNumber)
    }

    Surface(
        modifier
            .padding(horizontal = 24.dp)
            .wrapContentSize(),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    value = phoneNumber, // <-- Nilai ini sekarang akan reaktif
                    onValueChange = { },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_phonenumber),
                            contentDescription = "ic_phonenumber",
                            tint = colorResource(id = R.color.font2)
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Nomor Hp pengguna tidak tersedia",
                            fontSize = 13.sp
                        )
                    },
                    readOnly = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.White,
                        focusedIndicatorColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = colorResource(id = R.color.font3),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    value = note, // <-- Nilai ini sekarang akan reaktif
                    onValueChange = { },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_note),
                            contentDescription = "ic_note",
                            tint = colorResource(id = R.color.font2)
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Keterangan rumah tidak tersedia",
                            fontSize = 13.sp
                        )
                    },
                    readOnly = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.White,
                        focusedIndicatorColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    )
                )
            }
        }
    }
}