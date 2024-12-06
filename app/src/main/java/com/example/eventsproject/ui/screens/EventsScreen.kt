package com.example.eventsproject.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventsproject.network.RetrofitClient

@Composable
fun EventsScreen(navController: NavController, context: Context = LocalContext.current) {
    RetrofitClient.initialize(context, navController)

    // Estados
    var activeTab by remember { mutableStateOf(EventsTab.ComingSoon) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
//                SearchBar(searchQuery) { searchQuery = it }
                Spacer(modifier = Modifier.height(8.dp))
                Tabs(activeTab, onTabSelected = { activeTab = it })
            }
        }
    ) { paddingValues ->
        when (activeTab) {
            EventsTab.ComingSoon -> ComingSoonScreen(paddingValues, navController, searchQuery)
            EventsTab.History -> HistoryScreen(paddingValues, navController, searchQuery)
        }
    }
}

// -------- Search Bar --------

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(CircleShape)
            .background(Color(0xFFF0F0F0))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_search),
//                contentDescription = "Search",
//                tint = Color.Gray
//            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(text = "Search events...", color = Color.Gray)
                    }
                    innerTextField()
                }
            )
        }
    }
}

// -------- Tabs Component --------

@Composable
fun Tabs(activeTab: EventsTab, onTabSelected: (EventsTab) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        TabButton(
            text = "Coming Soon",
            isSelected = activeTab == EventsTab.ComingSoon,
            onClick = { onTabSelected(EventsTab.ComingSoon) }
        )
        TabButton(
            text = "Past Events",
            isSelected = activeTab == EventsTab.History,
            onClick = { onTabSelected(EventsTab.History) }
        )
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
        )
    ) {
        Text(text)
    }
}

// -------- EventsTab Enum --------

enum class EventsTab {
    ComingSoon,
    History
}
