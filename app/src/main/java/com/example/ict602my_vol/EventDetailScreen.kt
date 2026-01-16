package com.example.ict602my_vol

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ict602my_vol.data.VolEvent

// ===================== CUSTOM COLORS =====================
val PrimaryBackground = Color(0xFF3ABABE)
val DarkBackground = Color(0xFF267D7D)

// ===================== 1. EVENT DETAIL SCREEN =====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    event: VolEvent,
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Event Details", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PrimaryBackground)
        ) {
            // Section 1: Image & Name
            item { EventImageSection(event) }

            // Section 2: Date & Location
            item { EventInfoSection(event, context) }

            // Section 3: Organizer & Description
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryBackground)
                ) {
                    OrganizerSectionDetails(event)
                    AboutSection(event.description)

                    Spacer(Modifier.height(24.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        RegisterButton(onClick = onRegisterClick)
                    }
                    Spacer(Modifier.height(50.dp))
                }
            }
        }
    }
}

// ===================== 2. UI COMPONENTS =====================

@Composable
fun EventImageSection(event: VolEvent) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // LATEST: Using AsyncImage to load Firebase URL
            AsyncImage(
                model = event.imageUrl,
                contentDescription = "Event Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color.LightGray)
            )
        }

        Text(
            text = event.name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EventInfoSection(event: VolEvent, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Date with Calendar Intent
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { addEventToCalendar(context, event.name, event.date, event.location) }
                .padding(vertical = 8.dp)
        ) {
            Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(12.dp))
            Text(text = event.date, color = Color.Black, fontSize = 16.sp)
        }

        // Location with Maps Intent
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { openMaps(context, event.location) }
                .padding(vertical = 8.dp)
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(12.dp))
            Text(text = event.location, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun OrganizerSectionDetails(event: VolEvent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBackground)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.company_logo),
            contentDescription = "Organizer Logo",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text("ORGANIZED BY", color = Color.White, fontSize = 11.sp)
            Text(text = event.organizer, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AboutSection(description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text("About", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        // LATEST: Displays real description from Firebase
        Text(
            text = if (description.isNotEmpty()) description else "No additional information provided.",
            color = Color.Black,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun RegisterButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DarkBackground),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 16.dp)
    ) {
        Text("Register Now", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

// ===================== 3. LOGIC FUNCTIONS =====================

fun addEventToCalendar(context: Context, title: String, dateString: String, location: String) {
    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, title)
        putExtra(CalendarContract.Events.EVENT_LOCATION, location)
        putExtra(CalendarContract.Events.DESCRIPTION, "Volunteering for $title")
        putExtra(CalendarContract.Events.ALL_DAY, true)
    }
    context.startActivity(intent)
}

fun openMaps(context: Context, location: String) {
    val uri = Uri.parse("geo:0,0?q=${Uri.encode(location)}")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }
    context.startActivity(intent)
}

// ===================== 4. PREVIEW =====================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EventDetailsScreenPreview() {
    val dummyEvent = VolEvent(
        name = "Save Turtle",
        organizer = "Amanah.Co",
        date = "18 Jan 2026",
        location = "Pantai Batu Buruk",
        description = "Join us for a day of beach cleaning and turtle conservation activities at the beautiful Pantai Batu Buruk.",
        imageUrl = ""
    )
    EventDetailsScreen(event = dummyEvent, onBackClick = {}, onRegisterClick = {})
}