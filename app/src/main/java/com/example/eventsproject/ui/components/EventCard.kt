package com.example.eventsproject.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.example.eventsproject.network.RatingResponse
import com.example.eventsproject.network.RatingsRequest
import com.example.eventsproject.network.RetrofitClient
import com.example.eventsproject.types.Event
import com.example.eventsproject.types.Rating
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

@SuppressLint("SimpleDateFormat")
@Composable
fun EventCard(
    event: Event,
    isEditable: Boolean,
    navController: NavController,
    isAttendingInitially: Boolean = false
) {
    var showConfirmationModal by remember { mutableStateOf(false) } // State for confirmation modal visibility
    var showRatingModal by remember { mutableStateOf(false) } // State for rating modal visibility
    var eventStock by remember { mutableStateOf(event.stock) }
    var isAttending by remember { mutableStateOf(isAttendingInitially) }
    var userRating by remember { mutableStateOf(0) } // State for user rating
    var ratings by remember { mutableStateOf(event.ratings ?: emptyList()) } // Mutable list for ratings

    val averageRating = remember(ratings) {
        if (ratings.isNotEmpty()) ratings.map { it.rating }.average().toFloat() else 0f
    }

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
                style = MaterialTheme.typography.bodyMedium
            )

            // Location
            Text(
                text = "Location: ${event.location}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Stock
            Text(
                text = if (event.stock == 0) "Sold Out" else "Seats available: $eventStock",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (event.stock == 0) Color.Red else Color.Black
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Display average rating
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
//                Text(
//                    text = "Average Rating: ",
//                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
//                )
                DisplayStars(rating = averageRating ?: 5f)
            }

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
                                containerColor = Color(0xFF3B5998),
                                contentColor = Color.White
                            ),
                        ) {
                            Text("Confirm")
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (isAttending) {
                        Button(
                            onClick = { /* Handle action for attending */ },
                            modifier = Modifier.weight(2f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(if (eventDate > Date()) "Attending" else "Attended", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                if (isEditable) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { navController.navigate("edit_event_screen/${event.id}") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5998)),
                        enabled = event.stock > 0
                    ) {
                        Text("Edit", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { showRatingModal = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
                ) {
                    Text("Rate", style = MaterialTheme.typography.bodySmall)
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

    // Rating Modal
    if (showRatingModal) {
        AlertDialog(
            onDismissRequest = { showRatingModal = false },
            confirmButton = {
                Button(onClick = {
                    showRatingModal = false
                    coroutineScope.launch {
                        val newRating = submitRating(event.id, userRating)
                        if (newRating != null) {
                            ratings = ratings + newRating
                        }
                    }
                }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                Button(onClick = { showRatingModal = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Rate Event") },
            text = {
                Column {
                    Text("Rate this event from 1 to 5 stars:")
                    Row {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= userRating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Star $i",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        userRating = i // Update userRating on click
                                    },
                                tint = if (i <= userRating) Color(0xFFFFA726) else Color.Gray // Change star color
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun DisplayStars(rating: Float) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (i <= rating) Color(0xFFFFA726) else Color.Gray
            )
        }
    }
}

suspend fun submitRating(eventId: Int, rating: Int): Rating? {
    return withContext(Dispatchers.IO) {
        try {
            val request = RatingsRequest(
                eventId = eventId,
                rating = rating
            )
            val response = RetrofitClient.apiService.submitRating(request)
            if (response.isSuccessful) {
                response.body()
            } else {
                println("Failed to submit rating: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            println("Error submitting rating: ${e.message}")
            null
        }
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
