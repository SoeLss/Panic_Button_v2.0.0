package com.example.panicbuttonrtdb.prensentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.panicbuttonrtdb.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedTextFieldPassword(
    password: String,
    setPassword: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = {setPassword(it)},
        label = { Text(text = "Sandi") },
        visualTransformation =
        if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorResource(id = R.color.font),
            focusedLabelColor = colorResource(id = R.color.font),
            focusedLeadingIconColor = colorResource(id = R.color.font),
            unfocusedLeadingIconColor = colorResource(id = R.color.default_color),
            cursorColor = colorResource(id = R.color.font)
        ),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_password),
                contentDescription = "ic_password",
                modifier = Modifier.size(24.dp)
            )
        },
        trailingIcon = {
            val icon = if (passwordVisible) {
                painterResource(id = R.drawable.ic_hint_password)
            } else {
                painterResource(id = R.drawable.ic_hint_password)
            }

            IconButton(
                onClick = {
                    passwordVisible = !passwordVisible
                }
            ) {
                Icon(
                    painter = icon,
                    contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                    tint = if (passwordVisible) colorResource(id = R.color.font) else colorResource(
                        id = R.color.default_color
                    )
                )
            }
        }
    )
}