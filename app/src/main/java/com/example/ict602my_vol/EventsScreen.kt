package com.example.ict602my_vol

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage // For loading Firebase URLs
import com.example.ict602my_vol.data.VolEvent // Use updated central class

val EventScreenPrimaryColor = Color(0xFF36B8B1)
val EventScreenBadgeColor = Color(0xFF266668)

@Preview(showBackground = true)
@Composable
fun EventsScreenPreview() {
    val sampleEvents = listOf(
        VolEvent("1", "Save Turtle", "Amanah.Co", "12 Jan 2026", "Ipoh", "", ""),
        VolEvent("2", "Help Homeless", "MyHelper", "12 Jan 2026", "KL", "", ""),
    )
    EventsScreen(
        onBackClick = {},
        events = sampleEvents,
        onEventClick = {}
    )
}

// ===================== 1. EVENTS SCREEN =====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onBackClick: () -> Unit,
    events: List<VolEvent>, // Changed to VolEvent
    onEventClick: (VolEvent) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Events", color = Color.Black, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(EventScreenPrimaryColor)
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Badge
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(150.dp))
                            .background(EventScreenBadgeColor)
                            .padding(horizontal = 30.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.company_logo),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Available Events", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            items(events) { event ->
                EventCardExtended(event) { onEventClick(event) }
            }
        }
    }
}

// ===================== 2. EVENT CARD EXTENDED =====================
@Composable
fun EventCardExtended(event: VolEvent, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick)
    ) {
        Column {
            // UPDATED: Using AsyncImage for Firebase URL loading
            AsyncImage(
                model = event.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color.LightGray)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EventScreenBadgeColor) // Using badge color for footer
                    .padding(12.dp)
            ) {
                Text(event.organizer, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                Text(event.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, null, Modifier.size(14.dp), Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text(event.date, color = Color.White, fontSize = 12.sp)

                    Spacer(Modifier.width(16.dp))

                    Icon(Icons.Default.LocationOn, null, Modifier.size(14.dp), Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text(event.location, color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}