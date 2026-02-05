package com.example.panicbuttonrtdb.prensentation.screens

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.panicbuttonrtdb.R
import com.example.panicbuttonrtdb.notification.openNotificationSettings
import com.example.panicbuttonrtdb.prensentation.components.MonitorItem
import com.example.panicbuttonrtdb.prensentation.components.LatestMonitorItem
import com.example.panicbuttonrtdb.prensentation.components.LogOutAdmin
import com.example.panicbuttonrtdb.viewmodel.ViewModel

@Composable
fun DashboardAdminScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ViewModel,
    context: Context,
    onLogout: () -> Unit
) {
    val buzzerState by viewModel.buzzerState.observeAsState("off")
    var showDialog by remember { mutableStateOf(false) } // State untuk menampilkan dialog

    LaunchedEffect(Unit) {
        viewModel.getBuzzerState()
        viewModel.fetchLatestRecord() // Untuk MonitorItem utama
        viewModel.latestMonitorItem() // Untuk daftar data terbaru
    }

    BackHandler { //jika tombol back ditekan maka akan menutup activity
        (context as? Activity)?. finish()
    }

    Column(
        modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.primary)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 40.dp)
                    .align(Alignment.Center),
                textAlign = TextAlign.Center,
                text = "INFORMASI\nDARURAT",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = TextStyle(lineHeight = 40.sp)
            )
        }
        Spacer(modifier = Modifier.height(28.dp))

        MonitorItem(
            viewModel = viewModel,
            navController = navController,
            context = context,
            buzzerState = buzzerState
        )

        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.background))
        ) {
            Row(
                modifier
                    .padding(top = 16.dp, start = 24.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_warning),
                    contentDescription = "ic_warning",
                    tint = colorResource(id = R.color.primary)
                )
                Text(
                    text = "Data Terbaru",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(id = R.color.primary)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LatestMonitorItem(
                viewModel = viewModel,
                navController = navController
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Button(
                    onClick = { showDialog = true },
                    modifier
                        .padding(end = 8.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.background_button)
                    )
                ) {
                    Text(
                        text = "Settings",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorResource(id = R.color.font2)
                    )
                }

                Button(
                    onClick = {
                        navController.navigate("data_rekap")
                    },
                    modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.primary)
                    )
                ) {
                    Text(
                        text = "List Data Rekap",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "ic warning",
                        tint = colorResource(id = R.color.darurat),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Settings",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorResource(id = R.color.primary)
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                shape = RoundedCornerShape(100.dp),
                                color = colorResource(id = R.color.font2),
                                width = 1.dp
                            ),
                        onClick = { openNotificationSettings(context) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_notif),
                                contentDescription = "ic_notif",
                                tint = colorResource(id = R.color.primary),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Notifikasi",
                                fontSize = 12.sp,
                                color = colorResource(id = R.color.font2)
                            )
                        }
                    }
                    LogOutAdmin(onClick = onLogout)
                }
            },
            containerColor = Color.White,
            confirmButton = {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.background_button)
                    )
                ) {
                    Text(
                        "Kembali",
                        color = colorResource(id = R.color.font2)
                    )
                }
            }
        )
    }
}
