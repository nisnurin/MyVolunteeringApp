package com.example.ict602my_vol

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
// Firebase Imports
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AdminDashboardScreen(
    userViewModel: UserViewModel = viewModel(),
    onManageEventClick: () -> Unit,
    onViewReportClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {
    val tealColor = Color(0xFF4DB6AC)

    // Stats from ViewModel stay even when navigating
    val eventCount = userViewModel.adminEventCount
    val userCount = userViewModel.adminUserCount

    BackHandler { onLogout() }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onProfileClick,
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // Header Section
            Text(text = "Admin Dashboard", fontSize = 28.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Overview", fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            // STAT CARDS (Stacked vertically as per image)
            StatCard(number = eventCount.toString(), label = "EVENT", color = tealColor)
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(number = userCount.toString(), label = "USERS", color = tealColor)

            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Quick Action", fontSize = 18.sp, fontWeight = FontWeight.Normal)
            Spacer(modifier = Modifier.height(16.dp))

            // ACTION BUTTONS
            DashboardActionButton(
                text = "Manage Event",
                icon = Icons.Default.CalendarMonth,
                color = tealColor,
                onClick = onManageEventClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            DashboardActionButton(
                text = "View Report",
                icon = Icons.Default.Notifications,
                color = tealColor,
                onClick = onViewReportClick
            )
        }
    }
}

@Composable
private fun StatCard(number: String, label: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = number, fontSize = 40.sp, color = Color.Black)
            Text(text = label, fontSize = 24.sp, color = Color.Black)
        }
    }
}

@Composable
private fun DashboardActionButton(text: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = color
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(34.dp), tint = Color.Black)
            Text(text = text, fontSize = 24.sp, color = Color.Black)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminDashboardPreview() {
    // Replace 'EventTest3Theme' with your actual project theme name if it differs
    // Usually it is MyProjectNameTheme { ... }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AdminDashboardScreen(
            onManageEventClick = { /* No-op for preview */ },
            onViewReportClick = { /* No-op for preview */ },
            onProfileClick = { /* No-op for preview */ },
            onLogout = { /* No-op for preview */ }
        )
    }
}