package com.example.ict602my_vol

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import android.util.Base64
import com.example.ict602my_vol.UserViewModel
// Import your custom colors
import com.example.ict602my_vol.ui.theme.BrandBlue
import com.example.ict602my_vol.ui.theme.DarkBadge
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

data class NotificationItemData(
    val id: String,
    val eventId: String,
    val title: String,
    val message: String,
    val timestamp: Timestamp
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(padding: PaddingValues, userViewModel: UserViewModel) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser

    var notifications by remember { mutableStateOf<List<NotificationItemData>>(emptyList()) }
    
    // Get valid event data from UserViewModel to filter notifications
    val validEventIds = userViewModel.validEventIds
    val validEventNames = userViewModel.validEventNames

    // Process image data safely
    val imageModel = remember(userViewModel.profilePictureBase64) {
        if (!userViewModel.profilePictureBase64.isNullOrEmpty()) {
            try {
                val cleanBase64 = userViewModel.profilePictureBase64!!.trim()
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

    LaunchedEffect(currentUser, validEventIds, validEventNames) {
        if (currentUser != null) {
            val email = currentUser.email
            if (email != null) {
                db.collection("registrations")
                    .whereEqualTo("userEmail", email)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) return@addSnapshotListener
                        if (snapshot != null) {
                            val items = snapshot.documents.mapNotNull { doc ->
                                val eventId = doc.getString("eventId") ?: ""
                                val eventName = doc.getString("eventName") ?: "Event"
                                
                                // robust filtering: match by ID or Name
                                if (eventId in validEventIds || eventName in validEventNames) {
                                    val timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                                    NotificationItemData(
                                        id = doc.id,
                                        eventId = eventId,
                                        title = "Registration Successful",
                                        message = "You have successfully registered for $eventName",
                                        timestamp = timestamp
                                    )
                                } else {
                                    null
                                }
                            }
                            // Deduplicate to ensure count tallies with Profile/Activity screens (which show unique events)
                            .sortedByDescending { it.timestamp }
                            .distinctBy { if (it.eventId.isNotEmpty()) it.eventId else it.message }
                            
                            notifications = items
                        }
                    }
            }
        }
    }

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
                        model = imageModel,
                        contentDescription = null,
                        modifier = Modifier.size(35.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.location_pic),
                        error = painterResource(R.drawable.location_pic)
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

            item { Spacer(modifier = Modifier.height(20.dp)) }

            if (notifications.isEmpty()) {
                item {
                    Text(
                        text = "No notifications yet.",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                items(notifications) { notification ->
                    NotificationItem(notification)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationItemData) {
    val date = notification.timestamp.toDate()
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(date)

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
        Column {
            Text(
                text = notification.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = notification.message,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
             Text(
                text = formattedDate,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}