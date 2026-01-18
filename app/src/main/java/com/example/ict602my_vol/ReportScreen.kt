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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReportScreen(
    viewModel: ManageEventViewModel,
    onBackClick: () -> Unit
) {
    // Filter live data based on search query
    val registrationList = viewModel.registrations.filter {
        it.userName.contains(viewModel.searchQuery, ignoreCase = true) ||
                it.userEmail.contains(viewModel.searchQuery, ignoreCase = true) ||
                it.eventName.contains(viewModel.searchQuery, ignoreCase = true)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF1F1F1) // Outer gray background matching your design
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color.White)
        ) {
            // --- TOP BAR SECTION ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 12.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(28.dp),
                        tint = Color.Black
                    )
                }

                Text(
                    text = "REPORT",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // --- SEARCH BAR ---
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                placeholder = { Text("Search by name, email, or event") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                trailingIcon = {
                    if (viewModel.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery = "" }) {
                            Icon(Icons.Default.Close, "Clear search", tint = Color.Gray)
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF0F0F0),
                    focusedContainerColor = Color(0xFFF0F0F0),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFF3ABABE).copy(alpha = 0.3f)
                )
            )

            // --- REGISTRATION LIST ---
            if (registrationList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (viewModel.searchQuery.isEmpty())
                            "No registrations detected"
                        else "No results for \"${viewModel.searchQuery}\"",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        start = 24.dp,
                        end = 24.dp,
                        top = 8.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(registrationList) { record ->
                        UserReportItem(record)
                    }
                }
            }
        }
    }
}

@Composable
fun UserReportItem(record: RegistrationRecord) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // User Profile Icon
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFFE6E0FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color(0xFF6750A4),
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // User and Event Info
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                text = record.userName,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = record.userEmail,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "EVENT: ${record.eventName}",
                fontSize = 12.sp,
                color = Color(0xFF3ABABE), // Teal color from your theme
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}