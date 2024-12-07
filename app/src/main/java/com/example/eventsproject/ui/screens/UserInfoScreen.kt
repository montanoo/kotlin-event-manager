package com.example.eventsproject.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventsproject.network.LoginResponse
import com.example.eventsproject.utils.PreferenceManager
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val createdAt: String,
    val updatedAt: String
)

@Composable
fun UserInfoScreen(navController: NavController, context: Context = LocalContext.current) {
    val user = PreferenceManager.getLoginResponse(context)?.let { json ->
        Gson().fromJson(json, LoginResponse::class.java)?.user
    }

    fun formatDate(dateString: String?): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            dateString?.let {
                val date = inputFormat.parse(it)
                outputFormat.format(date ?: return "Invalid Date")
            } ?: "N/A"
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Color.LightGray)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFECEFF1)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Icon",
                    modifier = Modifier.size(50.dp),
                    tint = Color(0xFF3B5998)
                )
            }

            // Welcome Text
            Text(
                text = "Hello, ${user?.username ?: "Guest"}!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3B5998)
                ),
                textAlign = TextAlign.Center
            )

            // User Info
            if (user != null) {
                Text(user.email, style = MaterialTheme.typography.bodyLarge)
                Text("Account Created: ${formatDate(user.createdAt)}", style = MaterialTheme.typography.bodyMedium)
                Text("Last Updated: ${formatDate(user.updatedAt)}", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(
                    text = "No user data available.",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                    textAlign = TextAlign.Center
                )
            }

            // Logout Button
            Button(
                onClick = {
                    PreferenceManager.clearLoginData(context)
                    navController.navigate("login_screen")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5998)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Logout", color = Color.White)
            }
        }
    }
}
