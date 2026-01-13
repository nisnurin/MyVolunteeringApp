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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ict602my_vol.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun VolunteerScreen(onSignUpSuccess: () -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // 1. Panggil instance Firebase
    val context = LocalContext.current
    val auth = Firebase.auth
    val db = Firebase.firestore

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()) // Supaya boleh scroll jika skrin kecil
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_3ababe),
                contentDescription = "Logo",
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Join as Volunteer", fontSize = 22.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Fill in your details to start volunteering", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = nationality,
                onValueChange = { nationality = it },
                label = { Text("Nationality") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    auth.signInAnonymously().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid

                            if (userId != null) {
                                val userDetail = hashMapOf(
                                    "fullName" to fullName,
                                    "nationality" to nationality,
                                    "phone" to phone,
                                    "role" to "volunteer"
                                )

                                db.collection("users").document(userId)
                                    .set(userDetail)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Data Saved Successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        // INI PENTING: Panggil onSignUpSuccess supaya app tahu kena pindah screen
                                        onSignUpSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            context,
                                            "Error: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                            }
                        } else {
                            Toast.makeText(context, "Authentication Failed!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Sign Up")
            }
        }
    }
}

