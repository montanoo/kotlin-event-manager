package com.example.eventsproject.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.eventsproject.network.RetrofitClient
import com.example.eventsproject.types.Event
import kotlinx.coroutines.launch
import retrofit2.Response

data class EventRequest(
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val price: Float,
    val stock: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(eventId: Int?, context: Context = LocalContext.current) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var date by remember { mutableStateOf(TextFieldValue("")) }
    var time by remember { mutableStateOf(TextFieldValue("")) }
    var location by remember { mutableStateOf(TextFieldValue("")) }
    var price by remember { mutableStateOf(TextFieldValue("")) }
    var stock by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(eventId) {
        if (eventId != null) {
            isLoading = true
            coroutineScope.launch {
                try {
                    val response: Response<Event> = RetrofitClient.apiService.getEventById(eventId)
                    if (response.isSuccessful) {
                        response.body()?.let { event ->
                            title = TextFieldValue(event.title)
                            description = TextFieldValue(event.description)
                            date = TextFieldValue(event.date)
                            time = TextFieldValue(event.time)
                            location = TextFieldValue(event.location)
                            price = TextFieldValue(event.price.toString())
                            stock = TextFieldValue(event.stock.toString())
                        }
                    }
                } catch (e: Exception) {
                    println("Error fetching event: ${e.message}")
                } finally {
                    isLoading = false
                }
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Edit Event") })
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time (HH:MM:SS)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val eventRequest = EventRequest(
                                title = title.text,
                                description = description.text,
                                date = date.text,
                                time = time.text,
                                location = location.text,
                                price = price.text.toFloatOrNull() ?: 0f,
                                stock = stock.text.toIntOrNull() ?: 0
                            )
                            try {
                                val response = RetrofitClient.apiService.updateEvent(eventId!!, eventRequest)
                                if (response.isSuccessful) {
                                    println("Event updated successfully")
                                } else {
                                    println("Failed to update event: ${response.errorBody()?.string()}")
                                }
                            } catch (e: Exception) {
                                println("Error updating event: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}
