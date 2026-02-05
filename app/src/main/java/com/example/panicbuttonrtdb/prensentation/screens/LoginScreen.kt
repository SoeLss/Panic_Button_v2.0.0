package com.example.panicbuttonrtdb.prensentation.screens

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.panicbuttonrtdb.R
import com.example.panicbuttonrtdb.prensentation.components.OutlinedTextFieldPassword
import com.example.panicbuttonrtdb.viewmodel.ViewModel
import com.example.panicbuttonrtdb.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class) // <-- TAMBAHAN BARU
@Composable
fun LoginScreen(
    context: Context,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: ViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {

    BackHandler {
        (context as? Activity)?.finish()
    }
    var houseNumber by remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }  // Indikator loading

    // <-- AWAL BLOK TAMBAHAN BARU: State untuk dropdown perumahan -->
    var selectedPerumahanId by remember { mutableStateOf("") }
    var selectedPerumahanName by remember { mutableStateOf("Pilih Perumahan") }
    val daftarPerumahan by viewModel.daftarPerumahan.observeAsState(initial = emptyMap())
    var expanded by remember { mutableStateOf(false) }

    // <-- Ambil data perumahan saat layar pertama kali dibuka -->
    LaunchedEffect(Unit) {
        viewModel.fetchDaftarPerumahan()
    }
    // <-- AKHIR BLOK TAMBAHAN BARU -->

    Column(
        modifier
            .background(color = colorResource(id = R.color.primary))
            .fillMaxSize()
    ){
        Box(
            modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 60.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "ic_logo",
                modifier = Modifier.size(160.dp)
            )
        }
        Box(
            modifier
                .background(color = colorResource(id = R.color.primary))
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier
                    .height(600.dp)
                    .background(
                        color = Color.White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp)
            ){
                Column(
                    modifier
                        .padding(top = 40.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Login",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.font),
                    )
                    Spacer(modifier = Modifier.height(44.dp))

                    // <-- AWAL BLOK TAMBAHAN BARU: Dropdown untuk memilih perumahan -->
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedPerumahanName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Perumahan") },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_home), // Ganti dengan ikon yang sesuai
                                    contentDescription = "ic perumahan",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(id = R.color.font),
                                focusedLabelColor = colorResource(id = R.color.font),
                                focusedLeadingIconColor = colorResource(id = R.color.font),
                                unfocusedLeadingIconColor = colorResource(id = R.color.defauld),
                                cursorColor = colorResource(id = R.color.font)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            daftarPerumahan.forEach { (perumahanId, perumahanNama) ->
                                DropdownMenuItem(
                                    text = { Text(perumahanNama) },
                                    onClick = {
                                        selectedPerumahanId = perumahanId
                                        selectedPerumahanName = perumahanNama
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    // <-- AKHIR BLOK TAMBAHAN BARU -->

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = houseNumber,
                        onValueChange = {houseNumber = it},
                        label = { Text(text = "Nomor Rumah") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_home),
                                contentDescription = "ic home",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.font),
                            focusedLabelColor = colorResource(id = R.color.font),
                            focusedLeadingIconColor = colorResource(id = R.color.font),
                            unfocusedLeadingIconColor = colorResource(id = R.color.defauld),
                            cursorColor = colorResource(id = R.color.font)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextFieldPassword(password, setPassword)

                    Spacer(modifier = Modifier.height(12.dp))

                    // <-- BAGIAN INI DIUBAH TOTAL -->
                    Button(
                        onClick = {
                            if (selectedPerumahanId.isNotEmpty() && houseNumber.isNotEmpty() && password.isNotEmpty()) {
                                isLoading = true

                                viewModel.validateLogin(
                                    perumahanId = selectedPerumahanId,
                                    houseNumber = houseNumber,
                                    password = password
                                ) { success, user ->
                                    isLoading = false
                                    if (success && user != null) {
                                        // Navigasi berdasarkan role
                                        if (user.role == "admin") {
                                            navController.navigate("dashboard_admin")
                                        } else {
                                            navController.navigate("dashboard")
                                        }
                                    } else {
                                        Toast.makeText(context, "Login Gagal. Periksa kembali data Anda.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context,"Mohon isi semua kolom, termasuk perumahan", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                        ,
                        enabled = !isLoading, // Matikan tombol saat loading
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.font),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Login")
                    }

                    Spacer(modifier = Modifier.height(36.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Belum memiliki akun?"
                        )
                        Text(
                            modifier = Modifier
                                .clickable { navController.navigate("signup") },
                            text = "Daftar",
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.font)
                        )
                    }
                }
            }
        }
    }
}