package com.example.ict602my_vol.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ict602my_vol.R
import com.example.ict602my_vol.data.VolEvent
import com.example.ict602my_vol.data.Organizer
import com.example.ict602my_vol.EventsScreen
import com.example.ict602my_vol.EventDetailsScreen
import com.example.ict602my_vol.OrganizersScreen
import com.example.ict602my_vol.ManageEventViewModel

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    viewModel: ManageEventViewModel,
    onRegisterClick: (VolEvent) -> Unit,
    shouldReset: Boolean = false,
    onResetComplete: () -> Unit = {}
) {
    // Controller for nested navigation within the Home Tab
    val homeNavController = rememberNavController()

    // Organizers from ViewModel
    val organizers = viewModel.organizers

    // Handle reset signal (e.g., when clicking the Home icon in BottomBar)
    LaunchedEffect(shouldReset) {
        if (shouldReset) {
            homeNavController.navigate("event_list_screen") {
                popUpTo("event_list_screen") { inclusive = true }
            }
            onResetComplete()
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        NavHost(
            navController = homeNavController,
            startDestination = "event_list_screen",
            modifier = Modifier.fillMaxSize()
        ) {
            // Main Feed Screen
            composable("event_list_screen") {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    item { HeaderTitle("Event list") }

                    item { SearchBar(viewModel.searchQuery) { viewModel.searchQuery = it } }

                    item {
                        OrganizerSection(organizers) {
                            homeNavController.navigate("organizers_screen")
                        }
                    }

                    item {
                        EventHeader(onClick = { homeNavController.navigate("events_screen") })
                    }

                    // Empty Search Logic
                    if (viewModel.filteredEvents.isEmpty() && viewModel.searchQuery.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No events found for \"${viewModel.searchQuery}\"", color = Color.Gray)
                            }
                        }
                    } else {
                        // Displaying the events using the updated compact EventCard
                        items(viewModel.filteredEvents) { event ->
                            EventCard(event) {
                                homeNavController.navigate("event_details_screen/${event.id}")
                            }
                        }
                    }
                }
            }

            // Navigation Route: Event Details
            composable(
                route = "event_details_screen/{eventId}",
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { entry ->
                val eventId = entry.arguments?.getString("eventId")
                val selectedEvent = viewModel.filteredEvents.find { it.id == eventId }

                selectedEvent?.let {
                    EventDetailsScreen(
                        event = it,
                        onBackClick = { homeNavController.popBackStack() },
                        onRegisterClick = { onRegisterClick(it) }
                    )
                }
            }

            // Navigation Route: All Organizers
            composable("organizers_screen") {
                OrganizersScreen({ homeNavController.popBackStack() }, organizers)
            }

            // Navigation Route: Full Events List
            composable("events_screen") {
                EventsScreen(
                    onBackClick = { homeNavController.popBackStack() },
                    events = viewModel.filteredEvents
                ) { clicked ->
                    homeNavController.navigate("event_details_screen/${clicked.id}")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    val mockViewModel: ManageEventViewModel = viewModel()

    MaterialTheme {
        HomeScreen(
            paddingValues = PaddingValues(0.dp),
            viewModel = mockViewModel,
            onRegisterClick = {},
            shouldReset = false,
            onResetComplete = {}
        )
    }
}