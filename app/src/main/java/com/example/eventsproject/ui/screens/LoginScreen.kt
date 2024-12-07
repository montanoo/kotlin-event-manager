package com.example.eventsproject.ui.screens

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventsproject.network.GoogleTokenRequest
import com.example.eventsproject.network.LoginRequest
import com.example.eventsproject.network.LoginResponse
import com.example.eventsproject.network.RetrofitClient
import com.example.eventsproject.ui.components.EmailInput
import com.example.eventsproject.ui.components.PasswordInput
import com.example.eventsproject.ui.components.SignUpLink
import com.example.eventsproject.utils.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

@Composable
fun LoginScreen(navController: NavController, context: Context = LocalContext.current) {
    // Initialize Retrofit
    RetrofitClient.initialize(context, navController)

    // Retrieve saved login response
    val loginResponseJson = PreferenceManager.getLoginResponse(context)

    LaunchedEffect(loginResponseJson) {
        if (loginResponseJson != null) {
            navController.navigate("home_screen") {
                popUpTo("login_screen") { inclusive = true }
            }
        }
    }

    // State variables
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    // Configure Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("788085540899-habor0cevti2gvkfi6nbd0lh1r7in4kn.apps.googleusercontent.com") // Replace with your actual client ID
        .requestEmail()
        .build()

    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    // Google Sign-In Launcher
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            idToken?.let {
                coroutineScope.launch {
                    try {
                        val response = RetrofitClient.apiService.verifyGoogleToken(GoogleTokenRequest(idToken = it))
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            loginResponse?.let { res ->
                                // Save user data locally
                                PreferenceManager.saveLoginResponse(context, Gson().toJson(res))

                                // Navigate to home screen
                                withContext(Dispatchers.Main) {
                                    navController.navigate("home_screen") {
                                        popUpTo("login_screen") { inclusive = true }
                                    }
                                }
                            }
                        } else {
                            errorMessage = "Google Login failed: ${response.errorBody()?.string()}"
                        }
                    } catch (e: Exception) {
                        Log.e("GoogleAuth", "Error verifying token: ${e.message}")
                        errorMessage = "Error verifying token."
                    }
                }
            }
        } catch (e: ApiException) {
            println(e)
            Log.e("GoogleAuth", "Google Sign-In failed: ${e.statusCode}")
            errorMessage = "Google Sign-In failed."
        }
    }

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

    // UI Layout
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

            EmailInput(email = email, onEmailChange = { email = it })
            PasswordInput(password = password, onPasswordChange = { password = it })

            Button(
                onClick = { handleLogin() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5998)),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                Text(text = if (isLoading) "Logging in..." else "Login", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { coroutineScope.launch {
                    googleSignInClient.signOut().addOnCompleteListener {
                        launcher.launch(googleSignInClient.signInIntent)
                    }
                } },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDB4437)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Login with Google", color = Color.White)
            }

            SignUpLink(
                onClick = { navController.navigate("sign_up_screen") },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
