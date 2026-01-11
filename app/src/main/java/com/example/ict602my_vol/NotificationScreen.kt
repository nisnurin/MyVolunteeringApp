package com.example.ict602my_vol

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
// Import your custom colors
import com.example.ict602my_vol.ui.theme.BrandBlue
import com.example.ict602my_vol.ui.theme.DarkBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(padding: PaddingValues, userViewModel: UserViewModel) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Notification",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandBlue) // Main background is BRANDBLUE
                .padding(top = innerPadding.calculateTopPadding())
                .padding(bottom = padding.calculateBottomPadding())
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            // Inside your LazyColumn in NotificationScreen.kt
            item {
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clip(RoundedCornerShape(30.dp))
                        .background(DarkBadge)
                        .padding(vertical = 6.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Displays the SAME image picked in the Profile screen
                    AsyncImage(
                        model = userViewModel.selectedImageUri ?: R.drawable.location_pic,
                        contentDescription = null,
                        modifier = Modifier.size(35.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = userViewModel.userName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // --- 2. Notification Sections ---
            item { SectionTitle("Today") }
            item { NotificationItem("Event info updated") }
            item { NotificationItem("New volunteer opportunity") }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item { SectionTitle("Yesterday") }
            item { NotificationItem("Activity completed") }

            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { SectionTitle("This Week") }
            item { NotificationItem("Event reminder") }
            item { NotificationItem("New volunteer opportunity") }

            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { SectionTitle("This Month") }
            item { NotificationItem("Event reminder") }
            item { NotificationItem("New volunteer opportunity") }
            item { NotificationItem("Activity completed") }
            item { NotificationItem("Event info updated") }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 18.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun NotificationItem(text: String) {
    // This defines what the DARK BADGE notification bar looks like
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(DarkBadge)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.15f)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_info_details),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(6.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}