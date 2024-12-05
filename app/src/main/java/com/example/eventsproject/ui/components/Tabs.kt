package com.example.eventsproject.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

sealed class BottomNavItem(val route: String) {
    object ComingSoon : BottomNavItem("coming_soon_screen")
    object History : BottomNavItem("history_screen")
}


@Composable
fun Tabs(navController: NavController) {
    Row (
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button (onClick = { navController.navigate(BottomNavItem.ComingSoon.route) }) {
            Text("Coming soon")
        }
        Button(
            onClick = { navController.navigate(BottomNavItem.History.route) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text("History")
        }
    }
}