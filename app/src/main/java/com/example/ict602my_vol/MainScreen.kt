package com.example.ict602my_vol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(onGoogleClick: () -> Unit) {
    var selectedRole by remember { mutableStateOf("Volunteer") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tab Selector kat atas
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { selectedRole = "Volunteer" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole == "Volunteer") Color(0xFF3ABABE) else Color(0xFFF0F0F0),
                    contentColor = if (selectedRole == "Volunteer") Color.White else Color.Black
                ),
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Volunteer") }

            Button(
                onClick = { selectedRole = "Admin" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole == "Admin") Color(0xFF3ABABE) else Color(0xFFF0F0F0),
                    contentColor = if (selectedRole == "Admin") Color.White else Color.Black
                ),
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Admin") }
        }

        // Bahagian Kandungan
        if (selectedRole == "Volunteer") {
            VolunteerScreen() // Panggil borang volunteer
        } else {
            AdminScreen(
                onGoogleClick = onGoogleClick,
                onContinueClick = {
                    // Jika nak butang Continue di Admin bawa ke Volunteer tab:
                    selectedRole = "Volunteer"
                }
            )
        }
    }
}
