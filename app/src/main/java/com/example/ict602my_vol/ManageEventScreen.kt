package com.example.ict602my_vol

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ict602my_vol.data.VolEvent

// Standard Theme Color from your Home Screen
val AdminPrimaryColor = Color(0xFF3ABABE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageEventScreen(
    viewModel: ManageEventViewModel,
    onAddEventClick: () -> Unit,
    onEditEventClick: (VolEvent) -> Unit,
    onReportClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    BackHandler { onBackClick() }

    val eventList = viewModel.filteredEvents
    var showActionDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<VolEvent?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Events", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onReportClick) {
                        Icon(Icons.Default.Assessment, tint = AdminPrimaryColor, modifier = Modifier.size(28.dp), contentDescription = "Reports")
                    }
                    IconButton(onClick = onAddEventClick) {
                        Icon(Icons.Default.AddCircle, tint = AdminPrimaryColor, modifier = Modifier.size(32.dp), contentDescription = "Add Event")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Search Bar matching Home Screen style
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search events...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedBorderColor = Color.Transparent
                )
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Using the exact items logic from Home Screen
                items(eventList) { event ->
                    // This EventCard is coded exactly like the Home Screen version
                    EventCard(event) {
                        selectedEvent = event
                        showActionDialog = true
                    }
                }
            }
        }
    }

    // Admin Action Dialog
    if (showActionDialog && selectedEvent != null) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = { Text("Event Actions") },
            text = { Text("Choose an action for ${selectedEvent?.name}") },
            confirmButton = {
                Button(
                    onClick = {
                        onEditEventClick(selectedEvent!!)
                        showActionDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPrimaryColor)
                ) { Text("Edit") }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.deleteEvent(selectedEvent!!)
                    showActionDialog = false
                }) { Text("Delete", color = Color.Red) }
            }
        )
    }
}

/**
 * EXACT SAME CODE USED IN HOME SCREEN
 */
@Composable
fun EventCard(event: VolEvent, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // Top Image
            AsyncImage(
                model = event.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            // Bottom Teal Block (Matching Home Screen exactly)
            Column(
                modifier = Modifier
                    .background(AdminPrimaryColor)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = event.organizer.ifEmpty { "organizer name" },
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                Text(
                    text = event.name,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                // The 3 info rows
                EventMetadataRow(Icons.Default.CalendarToday, event.date)
                EventMetadataRow(Icons.Default.AccessTime, event.time)
                EventMetadataRow(Icons.Default.LocationOn, event.location)
            }
        }
    }
}

@Composable
fun EventMetadataRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.White
        )
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}