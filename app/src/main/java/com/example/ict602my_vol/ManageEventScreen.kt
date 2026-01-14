package com.example.ict602my_vol

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
// These two are the most important for your error:
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

@Composable
fun ManageEventScreen(
    viewModel: ManageEventViewModel,
    onBackClick: () -> Unit,
    onAddEventClick: () -> Unit
) {
    // State to handle the "Edit, Delete, View User" Pop-up
    var showDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<VolEvent?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // --- 1. Top Bar (Back, Title, Add Button) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                // Use Icons.AutoMirrored.Filled.ArrowBack
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Manage Event",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onAddEventClick) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Add Event",
                    tint = Color(0xFF3DB7B7),
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // --- 2. Search Bar ---
        OutlinedTextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            placeholder = { Text("search events") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F1F1),
                unfocusedContainerColor = Color(0xFFF1F1F1),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFF3DB7B7)
            )
        )

        // --- 3. Scrollable Event List ---
        // LazyColumn is the "RecyclerView" of Compose. It handles scrolling automatically.
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(viewModel.filteredEvents) { event ->
                EventCard(event = event, onClick = {
                    selectedEvent = event
                    showDialog = true
                })
            }
        }
    }

    // --- 4. The 3-Button Dialog (Edit, Delete, View User) ---
    if (showDialog && selectedEvent != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Actions for ${selectedEvent?.name}") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { /* Handle Edit */ showDialog = false },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3DB7B7))
                    ) { Text("Edit Event") }

                    Button(
                        onClick = {
                            viewModel.deleteEvent(selectedEvent!!)
                            showDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) { Text("Delete Event") }

                    Button(
                        onClick = { /* Handle View User */ showDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) { Text("View User") }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun EventCard(event: VolEvent, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // Placeholder for the Event Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.LightGray)
            )
            // Event Details Section
            Column(
                modifier = Modifier
                    .background(Color(0xFF3DB7B7)) // Matching Teal Color
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(text = "Organizer: ${event.organizer}", color = Color.White, fontSize = 12.sp)
                Text(text = event.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = event.date, color = Color.White, fontSize = 14.sp)
                Text(text = event.location, color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun ManageEventPreview() {
    // 1. Create a temporary instance of the ViewModel for the preview
    val previewViewModel = ManageEventViewModel()

    // 2. Call the screen with empty actions {} so it doesn't try to navigate
    ManageEventScreen(
        viewModel = previewViewModel,
        onBackClick = { /* Do nothing in preview */ },
        onAddEventClick = { /* Do nothing in preview */ }
    )
}