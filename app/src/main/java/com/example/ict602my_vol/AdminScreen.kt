package com.example.ict602my_vol.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ict602my_vol.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun AdminScreen(
    onGoogleClick: () -> Unit = {},
    onContinueSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val auth = Firebase.auth
    val db = Firebase.firestore
    val scrollState = rememberScrollState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState) // Membolehkan scroll supaya butang Google tak hilang
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_3ababe),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Create an Admin Account", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text("Enter your details to sign up", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        // --- SUSUNAN INPUT: NAME -> NATIONALITY -> PHONE -> EMAIL -> PASSWORD ---
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = nationality, onValueChange = { nationality = it }, label = { Text("Nationality") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- BUTTON CONTINUE: WARNA TEAL (0xFF3ABABE) ---
        Button(
            onClick = {
                if (name.isNotEmpty() && email.isNotEmpty() && nationality.isNotEmpty() && phoneNumber.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null){
                                val adminData = hashMapOf(
                                    "fullName" to name,
                                    "nationality" to nationality,
                                    "phoneNumber" to phoneNumber,
                                    "email" to email,
                                    "password" to password,
                                    "role" to "admin",
                                    "profile_picture" to null
                                )
                                db.collection("users").document(userId).set(adminData)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Admin Registered!", Toast.LENGTH_SHORT).show()
                                        onContinueSuccess()
                                    }
                            }
                        }
                        else {
                            Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6650a4)) // Warna Teal
        ) {
            Text("Continue", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Link Login
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already have an account?", fontSize = 14.sp, color = Color.Gray)
            TextButton(onClick = onNavigateToLogin) {
                Text("Log In", color = Color(0xFF3ABABE), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Divider
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text("  or  ", color = Color.Gray, fontSize = 14.sp)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- BUTTON GOOGLE: WARNA KELABU CAIR (0xFFF5F5F5) ---
        Button(
            onClick = onGoogleClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)) // Warna Kelabu Cair
        ) {
            Image(painter = painterResource(id = R.drawable.ic_google), contentDescription = "Google", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Continue with Google", fontSize = 16.sp, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "By clicking continue, you agree to our Terms of Service and Privacy Policy",
            fontSize = 11.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}