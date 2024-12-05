package com.example.eventsproject.ui.components

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.eventsproject.network.EventRequest
import com.example.eventsproject.types.Event
import java.text.SimpleDateFormat
import java.util.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@SuppressLint("NewApi")
@Composable
fun EventForm(
    event: Event? = null, // Pass an existing event for editing or null for creating
    onSave: (EventRequest) -> Unit,
    onCancel: () -> Unit
) {
    // State variables for each field
    var title by remember(event) { mutableStateOf(event?.title ?: "") }
    var description by remember(event) { mutableStateOf(event?.description ?: "") }

    val isoFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())

    val initialDate = event?.date?.let {
        ZonedDateTime.parse(it, isoFormatter).format(dateFormatter)
    } ?: ""
    val initialTime = event?.time?.let {
        ZonedDateTime.parse(it, isoFormatter).format(timeFormatter)
    } ?: ""

    var date by remember(event) { mutableStateOf(initialDate) }
    var time by remember(event) { mutableStateOf(initialTime) }
    var location by remember(event) { mutableStateOf(event?.location ?: "") }
    var price by remember(event) { mutableStateOf(event?.price?.toString() ?: "") }
    var stock by remember(event) { mutableStateOf(event?.stock?.toString() ?: "") }

    // Get the context
    val context = LocalContext.current

    // For the DatePicker and TimePicker dialogs
    val calendar = Calendar.getInstance()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title of the Form (Editing or Creating)
        Text(
            text = if (event == null) "Create New Event" else "Edit Event",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Title Field
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        // Description Field
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        // Date Picker Field
        OutlinedTextField(
            value = date,
            onValueChange = {},
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth)
                            date = dateFormatter.format(calendar.toInstant().atZone(ZonedDateTime.now().zone))
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                }
            }
        )

        // Time Picker Field
        OutlinedTextField(
            value = time,
            onValueChange = {},
            label = { Text("Time") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            time = timeFormatter.format(calendar.toInstant().atZone(ZonedDateTime.now().zone))
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                }) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Select Time")
                }
            }
        )

        // Location Field
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        // Price Field
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Stock Field
        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val parsedPrice = price.toDoubleOrNull() ?: 0.0
                    val parsedStock = stock.toIntOrNull() ?: 0
                    if (title.isNotEmpty() && description.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty() && location.isNotEmpty()) {
                        val eventRequest = EventRequest(
                            id = event?.id ?: 0, // Use existing event ID or 0 for a new event
                            title = title,
                            description = description,
                            date = date,
                            time = time,
                            location = location,
                            price = parsedPrice,
                            stock = parsedStock
                        )
                        onSave(eventRequest)
                    } else {
                        println("Please fill in all required fields.")
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save")
            }
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Cancel")
            }
        }
    }
}


