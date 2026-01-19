package com.example.ict602my_vol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ict602my_vol.data.RegistrationData

@Composable
fun ViewRegistrationScreen(
    data: RegistrationData,
    onBackToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- HEADER ---
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "MY VOLUNTEERING APP",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF008080),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // --- CARD HIJAU TEAL ---
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4DB6AC))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally // SEMUA CONTENT KE TENGAH
            ) {
                Text(
                    text = "My Registration",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(30.dp))

                // --- MAKLUMAT EVENT (CENTERED) ---
                RegistrationDetailItem(label = "Event Name", value = data.eventName)
                RegistrationDetailItem(label = "Location", value = data.location)
                RegistrationDetailItem(label = "Status", value = data.status)

                Spacer(modifier = Modifier.height(24.dp))

                // Garis pemisah halus
                Divider(color = Color.Black.copy(alpha = 0.1f), thickness = 1.dp)

                Spacer(modifier = Modifier.height(24.dp))

                // --- MAKLUMAT USER (CENTERED) ---
                RegistrationDetailItem(label = "Name", value = data.fullName)
                RegistrationDetailItem(label = "Email", value = data.email)

                // Gunakan variable yang ada dalam RegistrationData kau (emergencyContactNumber)
                RegistrationDetailItem(label = "Contact", value = data.emergencyContactNumber)

                Spacer(modifier = Modifier.weight(1f))

                // --- BUTTON BACK (CENTERED) ---
                Button(
                    onClick = onBackToHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = "Back to Home",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// Function kecil supaya kod kat atas tak serabut & maintain Center
@Composable
fun RegistrationDetailItem(label: String, value: String) {
    Text(
        text = "$label : $value",
        fontSize = 16.sp,
        color = Color.Black,
        textAlign = TextAlign.Center, // Paksa text ke tengah
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun ViewRegistrationScreenPreview() {
    ViewRegistrationScreen(
        data = RegistrationData(
            eventName = "Charity Fun Run",
            location = "KLCC Park",
            status = "Registered",
            fullName = "John Doe",
            email = "john.doe@example.com",
            emergencyContactNumber = "123-4567890"
        ),
        onBackToHome = {}
    )
}
