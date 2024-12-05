import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.eventsproject.ui.screens.EventDetailsScreen
import com.example.eventsproject.ui.screens.EventsScreen
import com.example.eventsproject.ui.screens.UserInfoScreen

@Composable
fun HomeScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Events.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Events.route) {
                EventsScreen(navController)
            }
            composable(BottomNavItem.User.route) {
                UserInfoScreen()
            }
            composable("event_details_screen/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
                if (eventId != null) {
                    EventDetailsScreen(navController = navController, eventId = eventId)
                }
            }
        }
    }
}
