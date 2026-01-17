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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val homeNavController = rememberNavController()

    val organizers = remember {
        listOf(
            Organizer("Amanah.Co", R.drawable.company_logo),
            Organizer("MyHelper", R.drawable.company_logo),
            Organizer("Runner", R.drawable.company_logo)
        )
    }

    LaunchedEffect(shouldReset) {
        if (shouldReset) {
            homeNavController.navigate("event_list_screen") {
                popUpTo("event_list_screen") { inclusive = true }
            }
            onResetComplete()
        }
    }

    // Apply padding here so it affects all screens in the NavHost
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        NavHost(
            navController = homeNavController,
            startDestination = "event_list_screen",
            modifier = Modifier.fillMaxSize()
        ) {
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

                    // Check if list is empty for better User Experience
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
                        items(viewModel.filteredEvents) { event ->
                            // This EventCard is the one we updated in HomeComponents.kt
                            // to show the 'time' field.
                            EventCard(event) {
                                homeNavController.navigate("event_details_screen/${event.id}")
                            }
                        }
                    }
                }
            }

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

            composable("organizers_screen") {
                OrganizersScreen({ homeNavController.popBackStack() }, organizers)
            }

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

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    // 1. Create a dummy ViewModel or mock data
    // Note: This assumes your ManageEventViewModel can be instantiated without a
    // real Firebase connection during preview, or that you use a local mock.
    val mockViewModel = ManageEventViewModel()

    // 2. Wrap in your App Theme
    // Replace 'YourAppTheme' with the actual theme name from your project
    MaterialTheme {
        HomeScreen(
            paddingValues = PaddingValues(0.dp), // Simulation of Scaffold padding
            viewModel = mockViewModel,
            onRegisterClick = {},
            shouldReset = false,
            onResetComplete = {}
        )
    }
}