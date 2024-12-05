package com.example.eventsproject.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PasswordInput(password: String, onPasswordChange: (String) -> Unit) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = { onPasswordChange(it) },
        label = { Text("Password") },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Password Visibility"
                )
            }
        },
        shape = MaterialTheme.shapes.medium, // Rounded corners
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF3B5998),
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color(0xFF3B5998)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewPasswordInput() {
    PasswordInput(password = "password123", onPasswordChange = {})
}
