package com.example.ict602my_vol

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ict602my_vol.ui.theme.BrandBlue
import com.example.ict602my_vol.ui.theme.DarkBadge

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun ProfileScreen(
    padding: PaddingValues, // Received from Scaffold in MainScreen
    userViewModel: UserViewModel,
    onNavigateToActivities: () -> Unit,
    onLogout: () -> Unit,
    onManageEventsClick: () -> Unit
) {
    val context = LocalContext.current
    val selectedIndex = 0

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            userViewModel.saveProfileImage(uri)
        }
    }

    var showEditAboutDialog by remember { mutableStateOf(false) }
    var tempAboutMe by remember { mutableStateOf("") }

    var showEditStoryDialog by remember { mutableStateOf(false) }
    var tempStory by remember { mutableStateOf("") }

    if (showEditStoryDialog) {
        AlertDialog(
            onDismissRequest = { showEditStoryDialog = false },
            title = { Text("Edit My Story") },
            text = {
                OutlinedTextField(
                    value = tempStory,
                    onValueChange = { tempStory = it },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    label = { Text("Tell your story") },
                    placeholder = { Text("e.g., I love volunteering because...") },
                    maxLines = 5
                )
            },
            confirmButton = {
                @Suppress("NAME_SHADOWING")
                TextButton(onClick = {
                    userViewModel.updateUserStory(tempStory) { success ->
                        if (success) {
                            showEditStoryDialog = false
                            Toast.makeText(context, "Story saved successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to save story", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditStoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditAboutDialog) {
        AlertDialog(
            onDismissRequest = { showEditAboutDialog = false },
            title = { Text("Edit About Me") },
            text = {
                OutlinedTextField(
                    value = tempAboutMe,
                    onValueChange = { tempAboutMe = it },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    label = { Text("Short Bio") },
                    maxLines = 3
                )
            },
            confirmButton = {
                @Suppress("NAME_SHADOWING")
                TextButton(onClick = {
                    userViewModel.updateUserProfile(
                        userViewModel.userName,
                        userViewModel.userNationality,
                        tempAboutMe
                    ) { success ->
                        if (success) {
                            showEditAboutDialog = false
                            Toast.makeText(context, "About Me saved successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to save About Me", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditAboutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding) // Ensures content is not hidden by BottomNav
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- TOP BAR WITH LOGOUT ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            IconButton(
                onClick = onLogout,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color.Red
                )
            }
        }

        // --- 1. THE BEAUTY TOGGLE ---
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
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
                            if (index == 1) onNavigateToActivities()
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

        // --- 2. AVATAR ---
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
            modifier = Modifier.size(140.dp),
            shape = CircleShape,
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            AsyncImage(
                model = userViewModel.selectedImageUri ?: R.drawable.location_pic,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .clickable { launcher.launch("image/*") },
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        // --- 3. EDIT PHOTO BUTTON ---
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = userViewModel.userName,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Icon(Icons.Default.Email, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(text = userViewModel.userEmail, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(text = userViewModel.userNationality, color = Color.Gray)
            }
        }

        // --- 5. BOTTOM SECTION (Now uses Weight to fill screen) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // This forces the box to take up all remaining space
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
                    // Linked to ViewModel stats
                    StatItem(count = userViewModel.registeredEventsCount.toString(), label = "Registered")
                    StatItem(count = userViewModel.availableEventsCount.toString(), label = "Available")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("My Story", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    IconButton(
                        onClick = {
                            tempStory = userViewModel.userStory
                            showEditStoryDialog = true
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Story",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    // This shows the userStory from database
                    Text(
                        text = userViewModel.userStory,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("About Me", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    IconButton(
                        onClick = {
                            tempAboutMe = userViewModel.userAboutMe
                            showEditAboutDialog = true
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit About Me",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = userViewModel.userAboutMe,
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