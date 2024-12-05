package com.example.eventsproject.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventsproject.R
import com.example.eventsproject.network.AttendanceResponse
import com.example.eventsproject.network.ConfirmAttendanceRequest
import com.example.eventsproject.network.RetrofitClient
import com.example.eventsproject.types.Event
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

@SuppressLint("SimpleDateFormat")
@Composable
fun EventCard(event: Event, isEditable: Boolean, navController: NavController, isAttendingInitially: Boolean = false) {
    var showConfirmationModal by remember { mutableStateOf(false) } // State for modal visibility
    var eventStock by remember { mutableStateOf(event.stock) }
    var isAttending by remember { mutableStateOf(isAttendingInitially) }

    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("event_details_screen/${event.id}") },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))

    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Placeholder image for event
            Image(
                painter = painterResource(id = R.drawable.placeholder_image),
                contentDescription = "Event Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Event Title
            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Date and Time
            Text(
                text = "Date: ${formatDate(event.date)} at ${formatTime(event.time)}",
                style = MaterialTheme.typography.bodySmall
            )

            // Location
            Text(
                text = "Location: ${event.location}",
                style = MaterialTheme.typography.bodySmall
            )

            // Stock
            Text(
                text = if (event.stock == 0) "Sold Out" else "Seats available: $eventStock",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (event.stock == 0) Color.Red else Color.Black
                )
            )

            // Buttons Row
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

                val eventDate: Date? = try {
                    dateFormat.parse(event.date)
                } catch (e: Exception) {
                    null
                }
                if (eventDate != null) {


                if (event.stock > 0 && !isAttending && eventDate > Date()) {
                    Button(
                        onClick = { showConfirmationModal = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B5998), // Primary color for "Confirm assistance"
                            contentColor = Color.White
                        ),
                    ) {
                        Text("Confirm")
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                    if (isAttending && eventDate > Date()) {
                        Button(
                            onClick = { /* Handle action for attending */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50), // Green color for "You will attend!"
                                contentColor = Color.White
                            )
                        ) {
                            Text("Registered")
                        }
                    }
                    if (isAttending && eventDate < Date()) {
                        Button(
                            onClick = { /* Handle action for attending */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50), // Green color for "You will attend!"
                                contentColor = Color.White
                            )
                        ) {
                            Text("Attended")
                        }
                    }
                }




                if (isEditable) {
                    Button(
                        onClick = { navController.navigate("edit_event_screen/${event.id}") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5998)),
                        enabled = event.stock > 0
                    ) {
                        Text("Edit", color = Color.White)
                    }
                }
            }
        }
    }

    // Confirmation Modal
    if (showConfirmationModal) {
        AlertDialog(
            onDismissRequest = { showConfirmationModal = false },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationModal = false
                        coroutineScope.launch {
                            val result = confirmAttendance(event.id)
                            if (result) {
                                eventStock = maxOf(eventStock - 1, 0)
                                isAttending = true
                            }
                        }
                    },
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmationModal = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Confirm Attendance") },
            text = {
                Text(
                    "Are you sure you want to confirm your attendance for the event \"${event.title}\"?"
                )
            }
        )
    }
}

suspend fun confirmAttendance(eventId: Int): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val request = ConfirmAttendanceRequest(eventId = eventId)
            val response: Response<AttendanceResponse> = RetrofitClient.apiService.confirmAttendance(request)
            if (response.isSuccessful) {
                println("Attendance confirmed successfully")
                true
            } else {
                println("Failed to confirm attendance: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            println("Error confirming attendance: ${e.message}")
            false
        }
    }
}


// Helper Functions
fun formatDate(isoDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return try {
        val date = inputFormat.parse(isoDate)
        outputFormat.format(date)
    } catch (e: Exception) {
        isoDate
    }
}

fun formatTime(isoDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return try {
        val date = inputFormat.parse(isoDate)
        outputFormat.format(date)
    } catch (e: Exception) {
        isoDate
    }
}
