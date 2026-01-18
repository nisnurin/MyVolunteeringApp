package com.example.ict602my_vol.ui.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException


@Composable
fun MainScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: (String) -> Unit,
    onGoogleClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedRole by remember { mutableStateOf("Volunteer") }

    // --- SETUP GOOGLE LOGIN (Kekal Sama) ---
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d("GoogleSignIn", "Success: ${account?.email}")
            onSignUpSuccess()
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Sign in failed: ${e.message}")
        }
    }

    // --- MAIN CONTENT ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tab Selector: Volunteer | Admin
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { selectedRole = "Volunteer" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole == "Volunteer") Color(0xFF3ABABE) else Color(0xFFF0F0F0)
                ),
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Volunteer", color = if (selectedRole == "Volunteer") Color.White else Color.Black)
            }

            Button(
                onClick = { selectedRole = "Admin" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole == "Admin") Color(0xFF3ABABE) else Color(0xFFF0F0F0)
                ),
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Admin", color = if (selectedRole == "Admin") Color.White else Color.Black)
            }
        }

        // Papar screen ikut role
        if (selectedRole == "Volunteer") {
            VolunteerScreen(
                onSignUpSuccess = onSignUpSuccess,
                onNavigateToLogin = { onNavigateToLogin("Volunteer") }
            )
        } else {

            AdminScreen(
                onGoogleClick = onGoogleClick,
                onNavigateToLogin = { onNavigateToLogin("Admin") },
                onContinueSuccess = {
                    onNavigateToLogin("Admin")
                    onSignUpSuccess()
                }
            )

        }
    }
}