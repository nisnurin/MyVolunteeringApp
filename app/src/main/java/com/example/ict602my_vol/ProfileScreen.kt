package com.example.ict602my_vol

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.ict602my_vol.ui.theme.BrandBlue
import com.example.ict602my_vol.ui.theme.DarkBadge // Make sure this is imported

@Composable
fun ProfileScreen(
    padding: PaddingValues,
    userViewModel: UserViewModel,
    onNavigateToActivities: () -> Unit // This is the trigger
) {
    // On this page, selectedIndex is always 0 (Profile)
    val selectedIndex = 0

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) userViewModel.selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. THE BEAUTY TOGGLE ---
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth()
                .height(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFF2F2F2))
                .padding(4.dp)
        ) {
            listOf("Profile", "Activities").forEachIndexed { index, title ->
                val isSelected = (index == selectedIndex)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .clickable {
                            // IF CLICK ACTIVITIES (index 1), GO TO NEW PAGE
                            if (index == 1) {
                                onNavigateToActivities()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) BrandBlue else Color.Gray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // --- 2. AVATAR WITH SOFT SHADOW ---
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
            modifier = Modifier.size(140.dp),
            shape = CircleShape,
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            AsyncImage(
                model = userViewModel.selectedImageUri ?: R.drawable.location_pic,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .clickable { launcher.launch("image/*") },
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        // --- 3. POLISHED BUTTON ---
        Button(
            onClick = { launcher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(text = "Edit Photo", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        // --- 4. USER INFO ---
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = userViewModel.userName, fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.Black)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(text = "Ali123abu@gmail.com", color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(text = "Kuala Terengganu", color = Color.Gray)
            }
        }

        // --- 5. BOTTOM SECTION ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp))
                .background(BrandBlue)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(DarkBadge)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(count = "1", label = "Group")
                    StatItem(count = "12", label = "Events")
                    StatItem(count = "8", label = "Rewards")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("About Me", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(
                    "Passionate volunteer currently exploring new social activities in Terengganu.",
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 8.dp),
                    lineHeight = 20.sp
                )
            }
        }
    }
}
@Composable
fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = label, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
    }
}