import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Events : BottomNavItem("home_screen", "Events", Icons.Default.Event)
    object User : BottomNavItem("user_screen", "User", Icons.Default.Person)
}