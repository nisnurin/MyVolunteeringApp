package com.example.ict602my_vol

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ict602my_vol.ui.theme.EventTest3Theme

@Composable
fun AdminDashboardScreen(
    onManageEventClick: () -> Unit,
    onViewReportClick: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val tealColor = Color(0xFF4DB6AC)

    // Handle system back button to trigger logout/back to main login
    BackHandler {
        onLogout()
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Stay here */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Handle notifications logic later */ },
                    icon = { Icon(Icons.Outlined.Notifications, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onLogout,
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile/Logout") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Admin Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Overview", color = Color.Gray, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Stats Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(modifier = Modifier.weight(1f), title = "12", subtitle = "Total Events", extra = "5 Upcoming", color = tealColor)
                StatCard(modifier = Modifier.weight(1f), title = "256", subtitle = "Users", extra = "", color = tealColor)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(modifier = Modifier.weight(1f), icon = Icons.Default.Image, subtitle = "Events Name", extra = "Date of the event", color = tealColor)
                StatCard(modifier = Modifier.weight(1f), title = "4", subtitle = "Pending", extra = "", color = tealColor)
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Quick Action", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons using the navigation parameters
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
private fun StatCard(
    modifier: Modifier,
    title: String? = null,
    icon: ImageVector? = null,
    subtitle: String,
    extra: String,
    color: Color
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (title != null) {
                Text(text = title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            } else if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.Black)
            }
            Text(text = subtitle, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            Text(text = extra, fontSize = 10.sp, color = Color.Black.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun DashboardActionButton(text: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = color
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp), tint = Color.Black)
            Text(text = text, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        }
    }
}

// PREVIEW SECTION - Fixed to include empty lambdas
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminDashboardPreview() {
    EventTest3Theme {
        AdminDashboardScreen(
            onManageEventClick = {},
            onViewReportClick = {},
            onLogout = {}
        )
    }
}