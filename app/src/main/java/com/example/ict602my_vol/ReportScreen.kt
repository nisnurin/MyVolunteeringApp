package com.example.ict602my_vol

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ReportScreen(
    viewModel: ManageEventViewModel,
    onBackClick: () -> Unit
) {
    // Filter live data based on search
    val registrationList = viewModel.registrations.filter {
        it.userName.contains(viewModel.searchQuery, ignoreCase = true) ||
                it.userEmail.contains(viewModel.searchQuery, ignoreCase = true) ||
                it.eventName.contains(viewModel.searchQuery, ignoreCase = true)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF1F1F1) // Outer gray background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars) // Adapts to user phone notches
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color.White)
        ) {
            Text(
                text = "REPORT",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 32.dp, start = 24.dp)
            )

            // Search Bar
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                placeholder = { Text("Search by name, email, or event") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF0F0F0),
                    focusedContainerColor = Color(0xFFF0F0F0),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            // Detected List
            if (registrationList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No registrations detected", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    // We use start, end, and bottom to be explicit and avoid any "TODO" errors
                    contentPadding = PaddingValues(
                        start = 24.dp,
                        end = 24.dp,
                        top = 8.dp,      // Small gap after the search bar
                        bottom = 80.dp   // Larger gap so the last item isn't hidden by the back button
                    ),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(registrationList) { record ->
                        UserReportItem(record)
                    }
                }
            }

            // Bottom Navigation
            Box(Modifier.fillMaxWidth().padding(start = 16.dp, bottom = 24.dp)) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}

@Composable
fun UserReportItem(record: RegistrationRecord) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.size(52.dp).clip(CircleShape).background(Color(0xFFE6E0FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, null, tint = Color(0xFF6750A4), modifier = Modifier.size(30.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(record.userName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(record.userEmail, fontSize = 12.sp, color = Color.Gray)
            Text(
                text = "EVENT: ${record.eventName}", // Detection result
                fontSize = 12.sp,
                color = Color(0xFF3ABABE),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

