package com.example.ict602my_vol.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ict602my_vol.R

@Composable
fun AdminScreen(
    onGoogleClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_3ababe),
            contentDescription = "Logo",
            modifier = Modifier.size(160.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Create an Account", fontSize = 22.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Enter your email to sign up", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Butang Continue ke Volunteer Page
        Button(
            onClick = onContinueClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Continue")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Divider(color = Color.Gray.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.weight(1f))
            Text("  or  ", color = Color.Gray, fontSize = 14.sp)
            Divider(color = Color.Gray.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Butang Google
        Button(
            onClick = onGoogleClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "Google",
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Continue with Google", fontSize = 16.sp, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Teks Terma & Syarat yang tertinggal
        Text(
            text = "By clicking continue, you agree to our Terms of Service and Privacy Policy",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}