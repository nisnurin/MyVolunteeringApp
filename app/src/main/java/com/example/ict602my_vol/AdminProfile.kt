package com.example.ict602my_vol

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun AdminProfileScreen(onHomeClick: () -> Unit, onLogout: () -> Unit) {
    val tealColor = Color(0xFF4DB6AC)

    // Firebase
    val auth = Firebase.auth
    val db = Firebase.firestore
    val currentUser = auth.currentUser

    // User Data State
    var name by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf(currentUser?.email ?: "Loading...") }
    var contact by remember { mutableStateOf("Loading...") }

    // Fetch Data
    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        name = document.getString("fullName") ?: "No Name"
                        val fsEmail = document.getString("email")
                        if (!fsEmail.isNullOrEmpty()) email = fsEmail
                        contact = document.getString("phoneNumber") ?: "No Contact"
                    }
                }
        }
    }

    // State to store the selected image Uri
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher to handle picking an image from the gallery/Drive
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
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
            Box(contentAlignment = Alignment.BottomCenter) {
                if (selectedImageUri != null) {
                    // Displays the selected image from URI (using Coil)
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Default placeholder image
                    Image(
                        painter = painterResource(id = R.drawable.location_pic),
                        contentDescription = "Default Profile",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                // Change Profile Button - Triggers Photo Picker
                Surface(
                    color = Color.Black,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .offset(y = 15.dp)
                        .clickable { photoPickerLauncher.launch("image/*") } // Launch picker
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Change Profile", color = Color.White, fontSize = 11.sp)
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Default.FileUpload,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(35.dp))
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
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(22.dp),
                    tint = Color.Black
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