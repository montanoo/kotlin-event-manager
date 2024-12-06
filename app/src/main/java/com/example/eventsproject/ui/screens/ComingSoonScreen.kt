package com.example.eventsproject.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.eventsproject.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.eventsproject.network.LoginResponse
import com.example.eventsproject.types.Event
import com.example.eventsproject.ui.components.EventCard
import com.example.eventsproject.utils.PreferenceManager
import com.google.gson.Gson


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComingSoonScreen(
    paddingValues: PaddingValues,
    navController: NavController,
    searchQuery: String,
    context: Context = LocalContext.current
) {

    val loginResponseJson = PreferenceManager.getLoginResponse(context)

    val loggedInUserId = remember(loginResponseJson) {
        loginResponseJson?.let {
            // Parse the JSON string into a LoginResponse object
            Gson().fromJson(it, LoginResponse::class.java)?.user?.id
        }
    }

    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = RetrofitClient.apiService.getComingSoonEvents()
                println(response)
                if (response.isSuccessful) {
                    events = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                println("Error fetching coming soon events: ${e.message}")
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(events.filter { it.title.contains(searchQuery, ignoreCase = true) }) { event ->
                EventCard(event, event.organizerId == loggedInUserId, navController, event.isAttendee)
            }
        }
    }
}

