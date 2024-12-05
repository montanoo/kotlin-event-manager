package com.example.eventsproject.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.eventsproject.network.EventRequest
import com.example.eventsproject.network.RetrofitClient
import com.example.eventsproject.types.Comment
import com.example.eventsproject.types.Event
import com.example.eventsproject.ui.components.EventForm
import kotlinx.coroutines.launch

@Composable
fun EditEventScreen(navController: NavController, eventId: Int) {
    var event by remember { mutableStateOf<Event?>(null) }
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(eventId) {
        coroutineScope.launch {
            try {
                val response = RetrofitClient.apiService.getEventDetails(eventId)
                if (response.isSuccessful) {
                    event = response.body()
                    comments = event?.comments ?: emptyList()
                    println("Body: ${response.body()}")
                } else {
                    println("Failed to fetch event details: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Error fetching event details: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
    EventForm (
        event = event,
        onSave = { eventRequest ->
            coroutineScope.launch {
                try {
                    val response = RetrofitClient.apiService.updateEvent(eventRequest)
                    if (response.isSuccessful) {
                        println("Event updated successfully!")
                        navController.popBackStack()
                    } else {
                        println("Failed to update event: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    println("Error updating event: ${e.message}")
                }
            }
        },
        onCancel = {
            navController.popBackStack() // Cancel and return
        }
    )
}

fun Event.toEventRequest(): EventRequest {
    return EventRequest(
        id = this.id,
        title = this.title,
        description = this.description,
        date = this.date,
        location = this.location,
        time = this.time,
        price = this.price,
        stock = this.stock
    )
}