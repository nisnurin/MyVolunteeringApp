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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ict602my_vol.data.VolEvent
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ict602my_vol.ui.theme.EventTest3Theme

// Match the color exactly to your other screens
val AdminPrimaryColor = Color(0xFF3ABABE)

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
        // TOP BAR SECTION
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Manage Event", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onAddEventClick) {
                Icon(
                    Icons.Default.AddCircle,
                    tint = AdminPrimaryColor,
                    modifier = Modifier.size(36.dp),
                    contentDescription = "Add Event"
                )
            }
        }

        // SEARCH BAR SECTION
        OutlinedTextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            placeholder = { Text("Search events...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F1F1),
                unfocusedContainerColor = Color(0xFFF1F1F1),
                unfocusedBorderColor = Color.Transparent
            )
        )

        // EVENTS LIST
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(viewModel.filteredEvents) { event ->
                AdminEventCard(event = event, onClick = {
                    selectedEvent = event
                    showDialog = true
                })
            }
        }
    }

    // ACTION DIALOG (EDIT/DELETE)
    if (showDialog && selectedEvent != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Actions for ${selectedEvent?.name}") },
            text = {
                Column {
                    Button(
                        onClick = { onEditEventClick(selectedEvent!!); showDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AdminPrimaryColor)
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
fun AdminEventCard(event: VolEvent, onClick: () -> Unit) {
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
            Column(modifier = Modifier.background(AdminPrimaryColor).fillMaxWidth().padding(12.dp)) {
                Text(text = "Organizer: ${event.organizer}", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                Text(text = event.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                // COMPACT STACKED INFO SECTION
                Column(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Line 1: Date
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, null, Modifier.size(13.dp), Color.White)
                        Text(text = " ${event.date}", color = Color.White, fontSize = 12.sp)
                    }

                    // Line 2: Time
                    if (event.time.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_recent_history),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(text = " ${event.time}", color = Color.White, fontSize = 12.sp)
                        }
                    }

                    // Line 3: Location
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, Modifier.size(13.dp), Color.White)
                        Text(text = " ${event.location}", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ManageEventScreenPreview() {
    EventTest3Theme {
        val mockViewModel: ManageEventViewModel = viewModel()
        ManageEventScreen(
            viewModel = mockViewModel,
            onAddEventClick = {},
            onEditEventClick = {},
            onBackClick = {}
        )
    }
}