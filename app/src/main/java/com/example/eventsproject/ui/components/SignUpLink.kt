package com.example.eventsproject.ui.components

import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier

@Composable
fun SignUpLink(onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text(
            text = "Don't have an account? Sign Up",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3B5998)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSignUpLink() {
    SignUpLink(onClick = {})
}
