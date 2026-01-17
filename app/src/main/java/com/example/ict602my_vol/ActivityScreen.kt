package com.example.ict602my_vol

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ict602my_vol.data.VolEvent
import com.example.ict602my_vol.ui.theme.BrandBlue
import com.example.ict602my_vol.ui.theme.DarkBadge

@Composable
fun ActivityScreen(
    padding: PaddingValues,
    onNavigateToProfile: () -> Unit
) {
    // UPDATED: Dummy Data now includes the 'time' parameter
    val registeredEvents = listOf(
        VolEvent(
            id = "1",
            name = "Amanah.Co",
            organizer = "Save Turtle",
            date = "18 Jan 2026",
            time = "08:00 AM", // Added time
            location = "Pantai Batu Buruk",
            description = "Protecting turtle nesting sites.",
            imageUrl = "https://example.com/turtle.jpg"
        ),
        VolEvent(
            id = "2",
            name = "Green World",
            organizer = "Mangrove Planting",
            date = "22 Feb 2026",
            time = "10:30 AM", // Added time
            location = "Setiu Wetlands",
            description = "Restoring the mangrove ecosystem.",
            imageUrl = ""
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(BrandBlue),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ActivityToggle(onNavigateToProfile)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                Text(
                    text = "Registered Event",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(registeredEvents) { event ->
                ActivityCard(event)
            }
        }
    }
}

@Composable
fun ActivityToggle(onNavigateToProfile: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth()
            .height(50.dp)
            .clip(CircleShape)
            .background(Color(0xFFF2F2F2))
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f).fillMaxHeight().clip(CircleShape)
                .clickable { onNavigateToProfile() },
            contentAlignment = Alignment.Center
        ) {
            Text("Profile", color = Color.Gray)
        }
        Box(
            modifier = Modifier
                .weight(1f).fillMaxHeight().clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text("Activities", color = BrandBlue, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActivityCard(event: VolEvent) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.LightGray)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBadge)
                    .padding(16.dp)
            ) {
                Text(text = "Organizer", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Text(text = event.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // UPDATED: Now displays both Date and Time
                    Text(
                        text = "${event.date} • ${event.time}",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = event.location, color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}