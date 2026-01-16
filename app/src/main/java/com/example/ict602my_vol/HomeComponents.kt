package com.example.ict602my_vol.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ict602my_vol.PrimaryBackground
import com.example.ict602my_vol.data.Event
import com.example.ict602my_vol.data.Organizer
import androidx.compose.ui.tooling.preview.Preview
import com.example.ict602my_vol.R

val ArrowColor = Color(0xFF8B8B8B)
// ===================== HEADER TITLE =====================
@Composable
fun HeaderTitle(title: String) {
    Text(
        text = title,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

// ===================== SEARCH =====================
@Composable
fun SearchBar(textState: TextFieldValue, onTextChange: (TextFieldValue) -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF0F0F0),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(48.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                value = textState,
                onValueChange = onTextChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = Color.Black),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (textState.text.isEmpty()) {
                            Text("Search", color = Color.Gray, fontSize = 16.sp)
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

// ===================== ORGANIZER SECTION =====================

@Composable
fun OrganizerSection(
    organizers: List<Organizer>,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(PrimaryBackground)
    ) {
        OrganizerSectionHeader(onViewAllClick = onViewAllClick)
        OrganizerList(organizers = organizers)
    }
}

// Header Organization

@Composable
fun OrganizerSectionHeader(onViewAllClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Organizers",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "View all",
            tint = Color.White,
            modifier = Modifier.clickable(onClick = onViewAllClick)
        )
    }
}

// list organization
@Composable
private fun OrganizerList(organizers: List<Organizer>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = organizers
        ) { organizer ->
            OrganizerItem(organizer)
        }
    }
}

@Composable
fun OrganizerItem(organizer: Organizer) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { /* Handle organizer click */ }
    ) {
        Image(
            painter = painterResource(id = organizer.imageResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            organizer.title,
            fontSize = 12.sp,
            maxLines = 1,
            color = Color.White
        )
    }
}

// ===================== EVENT HEADER =====================
@Composable
fun EventHeader(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Event", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "View all events", tint = ArrowColor)
    }
}

// ===================== EVENT CARD =====================

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            // 1. Event Image Section
            Image(
                painter = painterResource(id = event.imageResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

            // 2. Info Block Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        PrimaryBackground,
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .padding(12.dp)
            ) {
                // Organizer
                Text(event.organizer, color = Color.White, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))

                // Event Name
                Text(event.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))

                // Date and Location row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Ikon and Teks Date
                    Icon(Icons.Default.DateRange, contentDescription = "Date", modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text(event.date, color = Color.White, fontSize = 12.sp)

                    Spacer(Modifier.width(16.dp))

                    // Ikon and Teks Location
                    Icon(Icons.Default.LocationOn, contentDescription = "Location", modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text(event.location, color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FullHomeScreenPreview() {
    // 1. Sediakan data dummy untuk dipaparkan dalam Preview
    val dummyPadding = PaddingValues(0.dp)

    // 2. Panggil HomeScreen
    // Nota: Jika HomeScreen anda berada dalam fail yang berbeza,
    // pastikan fungsi ini diletakkan di tempat yang boleh mengakses data dummy tersebut.
    HomeScreen(
        paddingValues = dummyPadding,
        onRegisterClick = {},
        shouldReset = false,
        onResetComplete = {}
    )
}

@Preview(showBackground = true)
@Composable
fun EventCardPreview() {
    // Preview khusus untuk satu kad event sahaja
    val sampleEvent = Event(
        organizer = "Amanah.Co",
        name = "Save Turtle",
        date = "12 Jan 2026",
        location = "Ipoh",
        imageResId = R.drawable.location_pic
    )
    EventCard(event = sampleEvent, onClick = {})
}