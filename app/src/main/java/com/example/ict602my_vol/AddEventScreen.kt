package com.example.ict602my_vol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.ict602my_vol.data.VolEvent
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AddEventScreen(
    onNavigateBack: () -> Unit,
    eventToEdit: VolEvent? = null
) {
    val db = FirebaseFirestore.getInstance()
    val scrollState = rememberScrollState()

    var eventName by remember { mutableStateOf(eventToEdit?.name ?: "") }
    var date by remember { mutableStateOf(eventToEdit?.date ?: "") }
    var location by remember { mutableStateOf(eventToEdit?.location ?: "") }
    var organizer by remember { mutableStateOf(eventToEdit?.organizer ?: "") }
    var description by remember { mutableStateOf(eventToEdit?.description ?: "") }
    var imageUrl by remember { mutableStateOf(eventToEdit?.imageUrl ?: "") }

    var showSuccessDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = if (eventToEdit == null) "New Event" else "Edit Event", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = "MY VOLUNTEERING APP", fontSize = 12.sp, color = Color(0xFF3DB7B7))
        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF3DB7B7)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                FormInput("Event Name", eventName) { eventName = it }
                FormInput("Date", date) { date = it }
                FormInput("Location", location) { location = it }
                FormInput("Organizer", organizer) { organizer = it }
                FormInput("Image URL", imageUrl) { imageUrl = it }
                FormInput("Description", description, singleLine = false, modifier = Modifier.height(120.dp)) { description = it }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = onNavigateBack, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                        Text("Cancel", color = Color.Black)
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C)),
                        onClick = {
                            val data = hashMapOf(
                                "name" to eventName, "date" to date, "location" to location,
                                "organizer" to organizer, "description" to description, "imageUrl" to imageUrl
                            )
                            val task = if (eventToEdit == null) db.collection("events").add(data)
                            else db.collection("events").document(eventToEdit.id).set(data)

                            task.addOnSuccessListener { showSuccessDialog = true }
                        }
                    ) {
                        Text(if (eventToEdit == null) "Save" else "Update", color = Color.White)
                    }
                }
            }
        }
    }

    if (showSuccessDialog) {
        SuccessPopOut(isEdit = eventToEdit != null) {
            showSuccessDialog = false
            onNavigateBack()
        }
    }
}

@Composable
fun FormInput(label: String, value: String, singleLine: Boolean = true, modifier: Modifier = Modifier.height(55.dp), onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(label, color = Color.White, fontSize = 14.sp)
        TextField(
            value = value, onValueChange = onValueChange, modifier = modifier.fillMaxWidth().padding(top = 4.dp),
            singleLine = singleLine, shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
        )
    }
}

@Composable
fun SuccessPopOut(isEdit: Boolean, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.width(300.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = if (isEdit) "Event Updated\nSuccessfully" else "New Event\nSuccessfully Added", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C)), modifier = Modifier.fillMaxWidth(0.7f)) {
                    Text("Back", color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddEventScreenPreview() {
    AddEventScreen(onNavigateBack = {})
}