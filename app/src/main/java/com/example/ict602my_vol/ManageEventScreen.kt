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

// Standard Theme Color matches your Dashboard
val AdminPrimaryColor = Color(0xFF3ABABE)

@Composable
fun ManageEventScreen(
    viewModel: ManageEventViewModel,
    onAddEventClick: () -> Unit,
    onEditEventClick: (VolEvent) -> Unit,
    onReportClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    // Hardware back button returns to Dashboard
    BackHandler { onBackClick() }

    val searchQuery = viewModel.searchQuery
    val eventList = viewModel.filteredEvents

    var showActionDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<VolEvent?>(null) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(16.dp)
        ) {
            // --- TOP BAR ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Manage Events",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.weight(1f))

                // REPORT BUTTON
                IconButton(onClick = onReportClick) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        tint = AdminPrimaryColor,
                        modifier = Modifier.size(28.dp),
                        contentDescription = "Reports"
                    )
                }

                // ADD BUTTON
                IconButton(onClick = onAddEventClick) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        tint = AdminPrimaryColor,
                        modifier = Modifier.size(32.dp),
                        contentDescription = "Add Event"
                    )
                }
            }

            // --- SEARCH BAR ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                placeholder = { Text("Search events...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(28.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = AdminPrimaryColor.copy(alpha = 0.5f)
                )
            )

            // --- REAL-TIME EVENT LIST ---
            if (eventList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (searchQuery.isEmpty()) "No events found in Firestore" else "No results for \"$searchQuery\"",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(eventList) { event ->
                        AdminEventCard(event = event, onClick = {
                            selectedEvent = event
                            showActionDialog = true
                        })
                    }
                }
            }
        }
    }

    // --- ACTION DIALOG (Edit/Delete) ---
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

@Composable
fun AdminEventCard(event: VolEvent, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .background(AdminPrimaryColor)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = event.name.uppercase(),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                    Text(" ${event.location}", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}