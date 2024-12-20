package com.example.eventsproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventsproject.R
import com.example.eventsproject.types.Comment
import com.example.eventsproject.types.Event
import com.example.eventsproject.network.AddCommentRequest
import com.example.eventsproject.network.RetrofitClient
import com.example.eventsproject.types.Rating
import com.example.eventsproject.ui.components.DisplayStars
import com.example.eventsproject.ui.components.formatDate
import com.example.eventsproject.ui.components.formatTime
import com.example.eventsproject.ui.components.submitRating
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(navController: NavController, eventId: Int) {
    var event by remember { mutableStateOf<Event?>(null) }
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var newComment by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var userRating by remember { mutableStateOf(0) } // State for user's new rating
    var ratings by remember { mutableStateOf(emptyList<Rating>()) } // State for event ratings
    var showRatingModal by remember { mutableStateOf(false) } // State for rating modal

    // Calculate average rating
    val averageRating = remember(ratings) {
        if (ratings.isNotEmpty()) ratings.map { it.rating }.average().toFloat() else 0f
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(eventId) {
        coroutineScope.launch {
            try {
                val response = RetrofitClient.apiService.getEventDetails(eventId)
                if (response.isSuccessful) {
                    event = response.body()
                    comments = event?.comments ?: emptyList()
                    ratings = event?.ratings ?: emptyList() // Initialize ratings
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

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        event?.let { event ->
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(event.title) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Event Background and Title
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.placeholder_image),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                    )
                                ),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Text(
                                text = event.title,
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Event Information
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Date: ${formatDate(event.date)} at ${formatTime(event.time)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Location: ${event.location}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Average Rating Section
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "Average Rating:",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DisplayStars(rating = averageRating)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = String.format("%.1f", averageRating),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Button to Open Rating Modal
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Button(onClick = { showRatingModal = true }) {
                            Text("Rate Event")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Add Comment Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newComment,
                            onValueChange = { newComment = it },
                            label = { Text("Add a comment") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (newComment.isNotBlank()) {
                                coroutineScope.launch {
                                    try {
                                        val request = AddCommentRequest(eventId = eventId, content = newComment)
                                        val response = RetrofitClient.apiService.addComment(request)
                                        if (response.isSuccessful) {
                                            val newCommentResponse = response.body()
                                            newCommentResponse?.let {
                                                comments = listOf(it) + comments
                                                newComment = ""
                                            }
                                        } else {
                                            println("Failed to add comment: ${response.errorBody()?.string()}")
                                        }
                                    } catch (e: Exception) {
                                        println("Error adding comment: ${e.message}")
                                    }
                                }
                            }
                        }) {
                            Text("Submit")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Comments Section
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(comments) { comment ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // User Icon
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "User Icon",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color.LightGray),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Comment Content
                                    Column {
                                        // User Name
                                        Text(
                                            text = comment.user.username,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        // Comment Text
                                        Text(
                                            text = comment.content,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                // Dotted Line Divider
                                Divider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 1.dp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }

            // Rating Modal
            if (showRatingModal) {
                AlertDialog(
                    onDismissRequest = { showRatingModal = false },
                    confirmButton = {
                        Button(onClick = {
                            showRatingModal = false
                            coroutineScope.launch {
                                val newRating = submitRating(eventId, userRating)
                                if (newRating != null) {
                                    ratings = ratings + newRating // Add new rating to the list
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
                                            .clickable { userRating = i },
                                        tint = if (i <= userRating) Color(0xFFFFA726) else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}


