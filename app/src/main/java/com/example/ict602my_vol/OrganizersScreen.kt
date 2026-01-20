package com.example.ict602my_vol

import android.util.Base64
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.ict602my_vol.data.Organizer
import com.example.ict602my_vol.data.VolEvent

@Preview(showBackground = true)
@Composable
fun OrganizersScreenPreview() {
    val organizers = listOf(
        Organizer("Amanah.Co", ""),
        Organizer("MyHelper", ""),
        Organizer("Runner", ""),
        Organizer("Seaboree", ""),
        Organizer("Event Organizer", ""),
        Organizer("Scout", ""),
        Organizer("Eventify", ""),
        Organizer("CrewWorks", ""),
        Organizer("UniEvents", ""),
        Organizer("Voluntrix", ""),
        Organizer("Campus Hub", ""),
        Organizer("Youth Connect", ""),
        Organizer("ProActive", ""),
        Organizer("NextGen Events", ""),
    )
    OrganizersScreen(
        onBackClick = {
            // Tiada tindakan dalam Preview
        },
        // Guna senarai 'organizers' yang diimport dari MainActivity
        organizers = organizers
    )
}

// ===================== COLOUR =====================

val ScreenBackground = Color(0xFF1E2F33)
val PrimaryBack = Color(0xFF36B8B1)
val TopBarColor = Color.White

val PrimaryDesignColor = Color(0xFF33AAAD)

val OrganizerBadgeColor = Color(0xFF266668)

// ===================== 1. MAIN SCREEN ORGANIZERS =====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizersScreen(
    onBackClick: () -> Unit,
    organizers: List<Organizer>
) {
    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
            TopAppBar(
                title = { Text("Event Details", color = Color.Black, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopBarColor)
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryBack)
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // FIRST ITEM:= ORGANIZERS BOX
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (10).dp)
                        .padding(bottom = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(150.dp))
                            .background(OrganizerBadgeColor) // Warna #266668
                            .padding(horizontal = 50.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Logo
                        Image(
                            painter = painterResource(id = R.drawable.company_logo), // Ganti dengan ikon sebenar anda
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Organizers",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            items(organizers) { organizer ->
                OrganizerListItem(organizer)
            }
        }
    }
}

// ===================== 2. (Organizer List Item) =====================
@Composable
fun OrganizerListItem(organizer: Organizer) {
    // Process image data safely (decoding Base64 from database)
    val imageModel = remember(organizer.imageUrl) {
        if (!organizer.imageUrl.isNullOrEmpty()) {
            try {
                val cleanBase64 = organizer.imageUrl.trim()
                    .removePrefix("data:image/jpeg;base64,")
                    .removePrefix("data:image/png;base64,")
                Base64.decode(cleanBase64, Base64.DEFAULT)
            } catch (e: Exception) {
                R.drawable.location_pic
            }
        } else {
            R.drawable.location_pic
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                OrganizerBadgeColor,
                shape = RoundedCornerShape(80.dp) // Sudut bulat
            )
            .clickable { /* Handle click to view organizer profile */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon/Logo Organizer (Saiz 48dp)
        AsyncImage(
            model = imageModel,
            contentDescription = "Organizer Icon",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White),
            placeholder = painterResource(R.drawable.location_pic),
            error = painterResource(R.drawable.location_pic)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = organizer.title,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}