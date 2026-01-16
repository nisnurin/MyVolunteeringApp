package com.example.ict602my_vol

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ict602my_vol.data.VolEvent
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ict602my_vol.ui.theme.EventTest3Theme

@Composable
fun ManageEventScreen(
    viewModel: ManageEventViewModel,
    onAddEventClick: () -> Unit,
    onEditEventClick: (VolEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    BackHandler { onBackClick() }
    var showDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<VolEvent?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Manage Event", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onAddEventClick) {
                Icon(Icons.Default.AddCircle, tint = Color(0xFF3DB7B7), modifier = Modifier.size(36.dp), contentDescription = null)
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
                focusedContainerColor = Color(0xFFF1F1F1),
                unfocusedContainerColor = Color(0xFFF1F1F1),
                unfocusedBorderColor = Color.Transparent
            )
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(viewModel.filteredEvents) { event ->
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
            title = { Text("Actions for ${selectedEvent?.name}") },
            text = {
                Column {
                    Button(
                        onClick = { onEditEventClick(selectedEvent!!); showDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3DB7B7))
                    ) { Text("Edit Event") }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.deleteEvent(selectedEvent!!); showDialog = false },
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
fun EventCard(event: VolEvent, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(140.dp).background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.background(Color(0xFF3DB7B7)).fillMaxWidth().padding(12.dp)) {
                Text(text = "Organizer: ${event.organizer}", color = Color.White, fontSize = 11.sp)
                Text(text = event.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (event.description.isNotEmpty()) {
                    Text(text = event.description, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Text(text = "${event.date} | ${event.location}", color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ManageEventScreenPreview() {
    // In a real project, it is better to pass dummy data directly to a
    // stateless version of this screen to make previews more reliable.
    // For now, we instantiate the ViewModel.

    EventTest3Theme {
        // We use a dummy instance for the preview
        val mockViewModel: ManageEventViewModel = viewModel()

        ManageEventScreen(
            viewModel = mockViewModel,
            onAddEventClick = { /* Do nothing in preview */ },
            onEditEventClick = { /* Do nothing in preview */ },
            onBackClick = { /* Do nothing in preview */ }
        )
    }
}