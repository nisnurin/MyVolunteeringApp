package com.example.ict602my_vol.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ict602my_vol.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBackToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val auth = Firebase.auth

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- LOGO APP ---
            Spacer(modifier = Modifier.height(40.dp))
            Image(
                painter = painterResource(id = R.drawable.logo_3ababe), // Guna logo yang sama
                contentDescription = "App Logo",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "MY VOLUNTEERING APP",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF008080)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- INPUT FIELDS ---
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(), // Sembunyi password
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // --- BUTANG LOGIN ---
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Welcome Back!", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                } else {
                                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E5993)) // Warna ungu konsisten
            ) {
                Text("Log In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- BUTANG BALIK KE SIGN UP ---
            TextButton(onClick = onBackToSignUp) {
                Text(
                    text = "Don't have an account? Sign Up here",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}