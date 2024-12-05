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
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventsproject.ui.components.EmailInput
import com.example.eventsproject.ui.components.PasswordInput
import com.example.eventsproject.ui.components.NameInput
import com.example.eventsproject.ui.components.SignUpButton
import com.example.eventsproject.ui.components.BackToLoginLink
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.eventsproject.network.RetrofitClient
import com.example.eventsproject.network.SignUpRequest
import com.example.eventsproject.network.SignUpResponse
import com.example.eventsproject.utils.PreferenceManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Response

@Composable
fun SignUpScreen(navController: NavController, context: Context = LocalContext.current) {

    RetrofitClient.initialize(context)
    val loginResponseJson = PreferenceManager.getLoginResponse(context)

    LaunchedEffect(loginResponseJson) {
        if (loginResponseJson != null) {
            navController.navigate("home_screen") {
                popUpTo("sign_up_screen") { inclusive = true }
            }
        }
    }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    fun handleSignUp() {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            errorMessage = "Please fill in all fields"
            return
        }

        isLoading = true
        errorMessage = ""

        val signUpRequest = SignUpRequest(username, email, password)

        coroutineScope.launch {
            try {
                val response: Response<SignUpResponse> = RetrofitClient.apiService.signUpUser(signUpRequest)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        val responseJson = Gson().toJson(it)

                        PreferenceManager.saveLoginResponse(context, responseJson)

                        navController.navigate("home_screen") {
                            popUpTo("sign_up_screen") { inclusive = true }
                        }
                    }
                } else {
                    errorMessage = response.errorBody()?.string() ?: "Sign-up failed"
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
                text = "Create an Account",
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

            // Name Input
            NameInput(name = username, onNameChange = { username = it })

            // Email Input
            EmailInput(email = email, onEmailChange = { email = it })

            // Password Input
            PasswordInput(password = password, onPasswordChange = { password = it })

            // Sign-Up Button
            SignUpButton(onClick = {handleSignUp()}, isLoading)

            BackToLoginLink(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignUpScreen() {
    SignUpScreen(navController = rememberNavController())
}

