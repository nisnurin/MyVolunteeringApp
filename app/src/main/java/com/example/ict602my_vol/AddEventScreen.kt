package com.example.ict602my_vol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ict602my_vol.VolEvent
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AddEventScreen(
    onNavigateBack: () -> Unit,
    eventToEdit: VolEvent? = null
) {
    val db = FirebaseFirestore.getInstance()

    // Initialize states with existing data if editing, or empty if new
    var eventName by remember { mutableStateOf(eventToEdit?.name ?: "") }
    var date by remember { mutableStateOf(eventToEdit?.date ?: "") }
    var location by remember { mutableStateOf(eventToEdit?.location ?: "") }
    var organizer by remember { mutableStateOf(eventToEdit?.organizer ?: "Admin") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = if (eventToEdit == null) "New Event" else "Edit Event",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(text = "MY VOLUNTEERING APP", fontSize = 12.sp, color = Color(0xFF3DB7B7))

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF3DB7B7)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                FormInput(label = "Event Name", value = eventName) { eventName = it }
                FormInput(label = "Date", value = date) { date = it }
                FormInput(label = "Location", value = location) { location = it }
                FormInput(label = "Organizer", value = organizer) { organizer = it }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancel", color = Color.Black)
                    }

                    Button(
                        onClick = {
                            val eventData = hashMapOf(
                                "name" to eventName,
                                "date" to date,
                                "location" to location,
                                "organizer" to organizer
                            )

                            if (eventToEdit == null) {
                                db.collection("events").add(eventData)
                                    .addOnSuccessListener { showSuccessDialog = true }
                            } else {
                                db.collection("events").document(eventToEdit.id)
                                    .set(eventData)
                                    .addOnSuccessListener { showSuccessDialog = true }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (eventToEdit == null) "Save" else "Update", color = Color.White)
                    }
                }
            }
        }
    }

    if (showSuccessDialog) {
        SuccessPopOut(
            isEdit = eventToEdit != null,
            onDismiss = {
                showSuccessDialog = false
                onNavigateBack()
            }
        )
    }
}

@Composable
fun FormInput(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(label, color = Color.White, fontSize = 14.sp)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(55.dp).padding(top = 4.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun SuccessPopOut(isEdit: Boolean, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.width(300.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isEdit) "Event Updated\nSuccessfully" else "New Event\nSuccessfully Added",
                    textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C)),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("Back", color = Color.White)
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddEventScreenPreview() {
    com.example.ict602my_vol.ui.theme.EventTest3Theme {
        AddEventScreen(
            onNavigateBack = { },
            eventToEdit = null // Shows the "New Event" version
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Edit Mode Preview")
@Composable
fun EditEventScreenPreview() {
    com.example.ict602my_vol.ui.theme.EventTest3Theme {
        AddEventScreen(
            onNavigateBack = { },
            eventToEdit = VolEvent(
                id = "1",
                name = "Beach Cleanup",
                organizer = "Eco-Warriors",
                date = "25 Dec 2025",
                location = "Port Dickson"
            )
        )
    }
}