package com.example.ict602my_vol.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

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

    NavHost(
        navController = homeNavController,
        startDestination = "event_list_screen",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("event_list_screen") {
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(Color.White),
                contentPadding = paddingValues
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
                items(viewModel.filteredEvents) { event ->
                    EventCard(event) {
                        homeNavController.navigate("event_details_screen/${event.id}")
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    // In Preview, we provide a default PaddingValues
    val dummyPadding = PaddingValues(bottom = 56.dp) // Simulating BottomBar space

    // Note: If your ViewModel has complex Firebase logic in init {},
    // the preview might show an error.
    // Ideally, you'd use a stateless version for previews.

    // For now, we attempt to render with a default VM
    val mockViewModel: ManageEventViewModel = viewModel()

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        HomeScreen(
            paddingValues = dummyPadding,
            viewModel = mockViewModel,
            onRegisterClick = {}
        )
    }
}