package com.example.eventsproject.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoginButton(onClick: () -> Unit, isLoading: Boolean = false) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5998)),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading
    ) {
        Text(text = if (isLoading) "Logging in..." else "Login", color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginButton() {
    LoginButton(onClick = {})
}
