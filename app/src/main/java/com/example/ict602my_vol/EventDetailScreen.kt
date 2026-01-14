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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ict602my_vol.data.Event



// ===================== CUSTOM COLORS =====================
// #3ABABE 
val PrimaryBackground = Color(0xFF3ABABE)
val DarkBackground = Color(0xFF267D7D)





// ===================== PREVIEW =====================
@Preview(showBackground = true)
@Composable
fun EventDetailsScreenPreview() {
    val dummyEvent = Event(
        organizer = "Amanah.Co",
        name = "Save Turtle",
        date = "18 Jan 2026 (Sunday)",
        location = "Pantai Batu Buruk",
        imageResId = R.drawable.location_pic
    )

    EventDetailsScreen(
        event = dummyEvent,
        onBackClick = {},
        onRegisterClick = {}
    )
}

// ===================== 1. EVENT DETAIL SCREEN =====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    event: Event,
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val context = LocalContext.current
    val eventDetails = event

    // 1. Scaffold Utama
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
        // 3. LazyColumn for about
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PrimaryBackground)
        ) {
            // Item 1: Imej
            item {
                EventImageSection(eventDetails)
            }

            // Item 2: date and location
            item {
                EventInfoSection(eventDetails, context)
            }

            // Item 3: Background
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryBackground)
                ) {
                    OrganizerSectionDetails(eventDetails)

                    AboutSection()
                    Spacer(Modifier.height(24.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        RegisterButton(onClick = onRegisterClick)
                    }
                    // Padding bawah LazyColumn supaya butang di BottomBar tidak bertindih dengan teks
                    Spacer(Modifier.height(50.dp))
                }
            }
        }

    }
}

// ===================== 2. UI COMPONENT =====================

@Composable
fun EventImageSection(event: Event) {
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
            Image(
                painter = painterResource(id = event.imageResId),
                contentDescription = "Event Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        // Text EVENT NAME
        Text(
            text = event.name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}


@Composable
fun EventInfoSection(event: Event, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryBackground)
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // CALENDAR
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                addEventToCalendar(context, event.name, event.date, event.location)
            }
        ) {
            Icon(Icons.Default.DateRange, contentDescription = "Date", modifier = Modifier.size(24.dp), tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("${event.date} | 06:00 AM - 13:00 PM", color = Color.Black, fontSize = 16.sp)
        }
        Spacer(Modifier.height(12.dp))

        // LOCATION
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                openMaps(context, event.location)
            }
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = "Location", modifier = Modifier.size(24.dp), tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text(event.location, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun OrganizerSectionDetails(event: Event) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBackground) // Warna hijau/biru yang lebih gelap
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.company_logo),
            contentDescription = "Organizer Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        Spacer(Modifier.width(16.dp))

        Column {
            Text("ORGANIZED BY", color = Color.White, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text(event.organizer, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AboutSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text("About", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Description about the event. Ini adalah maklumat tambahan mengenai acara ini dan butiran lain yang berkaitan.",
            color = Color.Black,
            fontSize = 14.sp
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
        Text("Register", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

// ===================== 3. FUNCTION =====================


fun addEventToCalendar(context: Context, title: String, dateString: String, location: String) {


    // FOR REAL TIME
    val beginTime: Long = 1768677600000L
    val endTime: Long = 1768703400000L

    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, title)
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime)
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
        putExtra(CalendarContract.Events.EVENT_LOCATION, location)
        putExtra(CalendarContract.Events.ALL_DAY, false)
        // Set availability ke Busy supaya ia blok masa di kalendar
        putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
    }
    context.startActivity(intent)
}

fun openMaps(context: Context, location: String) {
    val encodedLocation = Uri.encode(location)
    val gmmIntentUri = Uri.parse("geo:0,0?q=$encodedLocation")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")

    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    } else {
        val browserIntent = Intent(Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/maps/search/?api=1&query=$encodedLocation"))
        context.startActivity(browserIntent)
    }
}

