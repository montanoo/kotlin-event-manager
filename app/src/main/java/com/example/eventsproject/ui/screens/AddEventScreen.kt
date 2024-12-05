package com.example.eventsproject.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.example.eventsproject.network.EventRequest
import com.example.eventsproject.network.RetrofitClient
import com.example.eventsproject.ui.components.EventForm
import kotlinx.coroutines.launch

@Composable
fun AddEventScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    EventForm(
        event = null, // No existing event; this is for adding a new event
        onSave = { eventRequest ->
            coroutineScope.launch {
                try {
                    val response = RetrofitClient.apiService.createEvent(eventRequest)
                    if (response.isSuccessful) {
                        println("Event created successfully!")
                        navController.popBackStack() // Navigate back after successful creation
                    } else {
                        println("Failed to create event: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    println("Error creating event: ${e.message}")
                }
            }
        },
        onCancel = {
            navController.popBackStack() // Navigate back without creating
        }
    )
}
