package com.example.ict602my_vol

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ict602my_vol.data.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsNoRegisterScreen(
    event: Event,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Event Details", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
        // Tiada bottomBar (Butang Register dibuang)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PrimaryBackground)
        ) {
            item {
                EventImageSection(event)
            }
            item {
                EventInfoSection(event, context)
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryBackground)
                ) {
                    OrganizerSectionDetails(event)
                    AboutSection()
                    // Ruang kosong di bawah
                    Spacer(Modifier.height(50.dp))
                }
            }
        }
    }
}