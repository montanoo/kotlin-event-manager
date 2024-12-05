package com.example.eventsproject

import HomeScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventsproject.ui.screens.*
import com.example.eventsproject.ui.theme.EventsProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventsProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationSetup()
                }
            }
        }
    }
}

@Composable
fun NavigationSetup() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login_screen"
    ) {
        composable(
            "login_screen",
            enterTransition = { slideInVertically(initialOffsetY = { 2000 }, animationSpec = tween(durationMillis = 500)) },
            exitTransition = { slideOutVertically(targetOffsetY = { -2000 }, animationSpec = tween(durationMillis = 500)) }
        ) {
            LoginScreen(navController = navController)
        }

        composable(
            "sign_up_screen",
            enterTransition = { slideInVertically(initialOffsetY = { -2000 }, animationSpec = tween(durationMillis = 500)) },
            exitTransition = { slideOutVertically(targetOffsetY = { 2000 }, animationSpec = tween(durationMillis = 500)) }
        ) {
            SignUpScreen(navController = navController)
        }

        composable(
            route = "home_screen",
            enterTransition = { slideInVertically(initialOffsetY = { 1000 }, animationSpec = tween(500)) },
            exitTransition = { slideOutVertically(targetOffsetY = { -1000 }, animationSpec = tween(500)) }
        ) {
            HomeScreen()
        }

    }

}
