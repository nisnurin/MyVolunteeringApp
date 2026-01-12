package com.example.ict602my_vol.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.ict602my_vol.data.RegistrationData

@Composable
fun ViewRegistrationScreen(data: RegistrationData, onBackToHome: () -> Unit, onBackToEvent: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(60.dp))
        // Logo & Nama App
        Text("MY VOLUNTEERING APP", fontWeight = FontWeight.Bold, color = Color(0xFF00796B), fontSize = 16.sp)

        Spacer(modifier = Modifier.height(30.dp))

        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF4DB6AC), shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp)) {
            Column(modifier = Modifier.padding(32.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("My Registration", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(40.dp))

                // Info Event
                Text("Event Name : ${data.eventName}", color = Color.Black)
                Text("Date : 21 December 2025", color = Color.Black)
                Text("Time : 9:00 AM - 1:00 PM", color = Color.Black)
                Text("Location : ${data.location}", color = Color.Black)
                Text("Status : ${data.status}", color = Color.Black)

                Spacer(modifier = Modifier.height(30.dp))

                // Info User
                Text("Name : ${data.fullName}", color = Color.Black)
                Text("Email : ${data.email}", color = Color.Black)
                Text("Contact : ${data.emergencyContactNumber}", color = Color.Black)


                Spacer(modifier = Modifier.height(40.dp))


                Button(onClick = onBackToHome, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) { Text("Back to Home") }
            }
        }
    }
}