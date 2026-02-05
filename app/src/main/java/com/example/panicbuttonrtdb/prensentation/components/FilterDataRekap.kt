package com.example.panicbuttonrtdb.prensentation.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPrioritas(
    modifier : Modifier = Modifier,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("Prioritas") }
    val options = listOf("Darurat", "Penting", "Biasa")


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier
            .padding(bottom = 8.dp)
    ) {
        OutlinedTextField(
            value = selectedOptionText,
            onValueChange = {},
            readOnly = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White
            ),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .width(170.dp)
                .height(50.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    clip = false,
                    ambientColor = Color.LightGray,
                    spotColor = Color.Black.copy(alpha = 0.5f)
                ),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            textStyle = TextStyle(lineHeight = 20.sp)

        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(
                            selectionOption,
                            style = TextStyle(lineHeight = 20.sp)
                        ) },
                    onClick = {
                        selectedOptionText = selectionOption
                        onSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterWaktu(
    modifier : Modifier = Modifier,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("Waktu") } //default filter
    val options = listOf("Terbaru", "Lama")


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier
            .padding(bottom = 8.dp)
    ) {
        OutlinedTextField(
            value = selectedOptionText,
            onValueChange = {},
            readOnly = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White
            ),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .width(170.dp)
                .height(50.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    clip = false,
                    ambientColor = Color.LightGray,
                    spotColor = Color.Black.copy(alpha = 0.5f)
                ),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            textStyle = TextStyle(lineHeight = 20.sp)

        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(
                            selectionOption,
                            style = TextStyle(lineHeight = 20.sp)
                        ) },
                    onClick = {
                        selectedOptionText = selectionOption
                        onSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}