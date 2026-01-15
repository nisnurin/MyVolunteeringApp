package com.example.ict602my_vol

import com.example.ict602my_vol.data.Event as VolEvent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler

@Composable
fun ManageEventScreen(
    viewModel: ManageEventViewModel,
    onAddEventClick: () -> Unit,
    onEditEventClick: (VolEvent) -> Unit,
    onBackClick: () -> Unit, // This is already being used by your IconButton
) {
    // ADD THIS LINE: It handles the physical/system back button
    BackHandler { onBackClick() }

    var showDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<VolEvent?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // This IconButton is already using onBackClick,
            // so it is now connected to the logic we put in MainActivity!
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            // ... (rest of your existing code)
            Text("Manage Event", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                Log.d("NAV", "Navigating to Add Event")
                onAddEventClick()
            }) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Add Event",
                    tint = Color(0xFF3DB7B7),
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        OutlinedTextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            placeholder = { Text("search events") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFF3DB7B7),
                focusedContainerColor = Color(0xFFF1F1F1),
                unfocusedContainerColor = Color(0xFFF1F1F1)
            )
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
            items(viewModel.filteredEvents) { event: VolEvent ->
                EventCard(event = event, onClick = {
                    selectedEvent = event
                    showDialog = true
                })
            }
        }
    }

    if (showDialog && selectedEvent != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Actions for ${selectedEvent?.name}") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            onEditEventClick(selectedEvent!!)
                            showDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3DB7B7))
                    ) { Text("Edit Event") }

                    Button(
                        onClick = {
                            viewModel.deleteEvent(selectedEvent!!)
                            showDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) { Text("Delete Event") }
                }
            },
            confirmButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun EventCard(
    event: VolEvent,
    onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(Color.LightGray))
            Column(modifier = Modifier.background(Color(0xFF3DB7B7)).fillMaxWidth().padding(12.dp)) {
                Text(text = "Organizer: ${event.organizer}", color = Color.White, fontSize = 12.sp)
                Text(text = event.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "${event.date} | ${event.location}", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@android.annotation.SuppressLint("ViewModelConstructorInComposable")
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun ManageEventPreview() {
    // 1. Create a dummy ViewModel for the preview
    val previewViewModel = ManageEventViewModel()

    // 2. Wrap in your app theme for correct colors
    com.example.ict602my_vol.ui.theme.EventTest3Theme {
        ManageEventScreen(
            viewModel = previewViewModel,
            onBackClick = { /* Do nothing in preview */ },
            onAddEventClick = { /* Do nothing in preview */ },
            onEditEventClick = { event -> /* Do nothing in preview */ }
        )
    }
}