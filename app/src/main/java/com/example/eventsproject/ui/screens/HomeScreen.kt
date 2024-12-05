

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eventsproject.ui.screens.AddEventScreen
import com.example.eventsproject.ui.screens.EditEventScreen
import com.example.eventsproject.ui.screens.EventDetailsScreen
import com.example.eventsproject.ui.screens.EventsScreen
import com.example.eventsproject.ui.screens.UserInfoScreen

@Composable
fun HomeScreen() {
    val navController = rememberNavController()

    // Get the current route
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            // Show FAB only on "home_screen"
            if (currentRoute == "home_screen") {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("add_event")
                    },
                    containerColor = Color(0xFF4CAF50) // Optional: Change FAB color
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Event",
                        tint = Color.White // Optional: Change icon color
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home_screen",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(
                route = "home_screen",
                enterTransition = {
                    slideInVertically (initialOffsetY = { 1000 }, animationSpec = tween(500))
                },
                exitTransition = {
                    slideOutVertically (targetOffsetY = { -1000 }, animationSpec = tween(500))
                }
            ) {
                EventsScreen(navController)
            }
            composable("user_screen") {
                UserInfoScreen()
            }
            composable("event_details_screen/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
                if (eventId != null) {
                    EventDetailsScreen(navController = navController, eventId = eventId)
                }
            }
            composable("edit_event_screen/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
                if (eventId != null) {
                    EditEventScreen(navController, eventId)
                }
            }
            composable("add_event") { AddEventScreen(navController) }
        }
    }
}
