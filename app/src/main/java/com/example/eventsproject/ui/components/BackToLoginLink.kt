package com.example.eventsproject.ui.components

import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BackToLoginLink(onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = { onClick() }, modifier = modifier) {
        Text("Already have an account? Log In")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBackToLoginLink() {
    BackToLoginLink(onClick = {})
}
