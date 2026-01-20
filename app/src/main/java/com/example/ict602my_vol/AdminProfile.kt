package com.example.ict602my_vol

import android.content.Context
import android.net.Uri
import com.example.ict602my_vol.utils.uriToBase64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
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
import coil.compose.AsyncImage // You may need to add this implementation to build.gradle
import com.example.ict602my_vol.ui.theme.EventTest3Theme

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun AdminProfileScreen(
    userViewModel: UserViewModel = viewModel(),
    onHomeClick: () -> Unit,
    onLogout: () -> Unit
) {
    val tealColor = Color(0xFF4DB6AC)

    // Firebase
    val currentUser = Firebase.auth.currentUser

    // Use state from ViewModel
    val name = userViewModel.userName
    val email = userViewModel.userEmail
    val contact = userViewModel.userPhone
    val profilePictureBase64 = userViewModel.profilePictureBase64

    val context = LocalContext.current

    // Launcher to handle picking an image from the gallery/Drive
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val base64String = uriToBase64(context, uri)
            if (base64String != null) {
                userViewModel.saveAdminProfileImage(base64String)
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = false,
                    onClick = onHomeClick,
                    icon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
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

        // --- TEAL HEADER SECTION ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(tealColor)
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "ORGANIZER TEAM",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(15.dp))

            // Profile Image Container
            Surface(
                modifier = Modifier.size(110.dp),
                shape = CircleShape,
                border = BorderStroke(2.dp, Color.White),
                color = Color.White
            ) {
                if (!profilePictureBase64.isNullOrEmpty()) {
                    val imageUri = "data:image/jpeg;base64,$profilePictureBase64"
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .clickable { photoPickerLauncher.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Default placeholder image
                    Image(
                        painter = painterResource(id = R.drawable.location_pic),
                        contentDescription = "Default Profile",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .clickable { photoPickerLauncher.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Change Profile Button - Matches ProfileScreen.kt style
            Button(
                onClick = { photoPickerLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    Icons.Default.FileUpload,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(text = "Change Profile", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(25.dp))
            Text("Volunteer Platform For All Community", color = Color.Black, fontSize = 14.sp)
            Text(
                "#VolunteerForAll",
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // --- ABOUT SECTION ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp, vertical = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "About This Organizer",
                    color = tealColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = tealColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminFieldItem(label = "Name", value = name)
                    AdminFieldItem(label = "Email", value = email)
                    AdminFieldItem(label = "Contact", value = contact)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
}

@Composable
fun AdminFieldItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.Black,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(4.dp))
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = value, modifier = Modifier.padding(12.dp), color = Color.Black)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminProfilePreview() {
    EventTest3Theme {
        AdminProfileScreen(onHomeClick = {}, onLogout = {})
    }
}