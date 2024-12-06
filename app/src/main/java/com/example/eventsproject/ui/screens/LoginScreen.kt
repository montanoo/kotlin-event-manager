package com.example.eventsproject.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.eventsproject.network.LoginRequest
import com.example.eventsproject.network.LoginResponse
import com.example.eventsproject.network.RetrofitClient
import com.example.eventsproject.ui.components.EmailInput
import com.example.eventsproject.ui.components.PasswordInput
import com.example.eventsproject.ui.components.SignUpLink
import com.example.eventsproject.utils.PreferenceManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Response

@Composable
fun LoginScreen(navController: NavController, context: Context = LocalContext.current) {

    RetrofitClient.initialize(context, navController)
    val loginResponseJson = PreferenceManager.getLoginResponse(context)

    LaunchedEffect(loginResponseJson) {
        if (loginResponseJson != null) {
            navController.navigate("home_screen") {
                popUpTo("login_screen") { inclusive = true }
            }
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    fun handleLogin() {
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage = "Please fill in all fields"
            return
        }

        isLoading = true
        errorMessage = ""

        val loginRequest = LoginRequest(email, password)

        coroutineScope.launch {
            try {
                val response: Response<LoginResponse> = RetrofitClient.apiService.loginUser(loginRequest)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        val responseJson = Gson().toJson(it)

                        PreferenceManager.saveLoginResponse(context, responseJson)

                        navController.navigate("home_screen") {
                            popUpTo("login_screen") { inclusive = true }
                        }
                    }
                } else {
                    errorMessage = response.errorBody()?.string() ?: "Login failed"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }


    // ---------------- UI ----------------
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

            Text(
                text = "Welcome to Eventify",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3B5998)
                ),
                textAlign = TextAlign.Center
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Email Input
            EmailInput(email = email, onEmailChange = { email = it })

            // Password Input
            PasswordInput(password = password, onPasswordChange = { password =  it})

            // Login Button
            Button(
                onClick = { handleLogin() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5998)),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                Text(text = if (isLoading) "Logging in..." else "Login", color = Color.White)
            }

            SignUpLink(
                onClick = { navController.navigate("sign_up_screen") },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(navController = rememberNavController())
}
