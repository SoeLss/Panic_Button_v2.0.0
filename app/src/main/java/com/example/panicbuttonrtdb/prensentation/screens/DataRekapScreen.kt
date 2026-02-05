package com.example.panicbuttonrtdb.prensentation.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.panicbuttonrtdb.R
import com.example.panicbuttonrtdb.prensentation.components.DataRekapItem
import com.example.panicbuttonrtdb.prensentation.components.FilterPrioritas
import com.example.panicbuttonrtdb.prensentation.components.FilterWaktu
import com.example.panicbuttonrtdb.viewmodel.ViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DataRekapScreen(
    modifier: Modifier = Modifier,
    viewModel: ViewModel,
    navController: NavController
) {
    val monitor by viewModel.monitorData.observeAsState(emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedPrioritas by remember { mutableStateOf("Prioritas") } // default pada filter
    val dateFormat = SimpleDateFormat("yyyy-MM-dd 'waktu' HH:mm", Locale.getDefault())
    var selectedWaktu by remember { mutableStateOf("Waktu") }
    val filterData = monitor
        .filter { it.houseNumber.contains(searchQuery, ignoreCase = true) } // filter berdasarkan houseNumber dan searchQuery
        .filter { selectedPrioritas == "Prioritas" || it.priority == selectedPrioritas } //filter berdasarkan prioritas yang dipilih
        .sortedBy {
            if (selectedWaktu == "Lama") {
                //urutkan dari yang paling lama
                dateFormat.parse(it.time)?.time ?: 0L
            } else {
                //urutkan dari yang paling baru
                -(dateFormat.parse(it.time)?.time ?: 0L)
            }
        }
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.fetchMonitorData()
            delay(2000)
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.background))
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(colorResource(id = R.color.primary))
                .padding(top = 40.dp, start = 26.dp, end = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "List Data Rekap",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier.height(26.dp))
            SearchDataRekap(
                query = searchQuery,
                onQueryChange = { newQuery ->
                    searchQuery = newQuery
                },
                onSearch = {

                }
            )
        }
        Column(
            modifier
                .background(color = colorResource(id = R.color.background))
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                FilterPrioritas{ selectedPrioritas = it}
                FilterWaktu{ selectedWaktu = it}
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterData) { log ->
                    DataRekapItem( log, navController = navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDataRekap(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Box(
        modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false,
                ambientColor = Color.LightGray,
                spotColor = Color.Black.copy(alpha = 0.5f)
            )
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(color = Color.White)
    ) {
        Row(
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        "Cari nomor rumah",
                        color = colorResource(id = R.color.font3),
                        style = TextStyle(lineHeight = 20.sp)
                    )
                },
                colors = TextFieldDefaults.colors(
                    // Di Material 3, containerColor dipisah per-kondisi
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,

                    // Parameter lainnya sebagian besar sama
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = colorResource(id = R.color.font2),
                    unfocusedTextColor = colorResource(id = R.color.font2)
                ),
                singleLine = true,
                modifier = Modifier
                    .height(46.dp)
                    .weight(1f),
                textStyle = TextStyle(lineHeight = 20.sp)
            )
            IconButton(
                onClick = onSearch,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "ic_search",
                    tint = colorResource(id = R.color.font2)
                )
            }
        }
    }
}