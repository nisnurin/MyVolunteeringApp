package com.example.ict602my_vol.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ict602my_vol.R
import com.example.ict602my_vol.data.Event
import com.example.ict602my_vol.data.Organizer
import com.example.ict602my_vol.EventsScreen
import com.example.ict602my_vol.EventDetailsScreen
import com.example.ict602my_vol.OrganizersScreen

// ===================== HOME SCREEN =====================
@Composable
fun HomeScreen(paddingValues: PaddingValues,
               onRegisterClick: () -> Unit,
               shouldReset: Boolean = false,
               onResetComplete: () -> Unit = {}
) {
    val homeNavController = rememberNavController()

    // --- SAMPLE DATA  ---
    val organizers = listOf(
        Organizer("Amanah.Co", R.drawable.company_logo),
        Organizer("MyHelper", R.drawable.company_logo),
        Organizer("Runner", R.drawable.company_logo),
        Organizer("Seaboree", R.drawable.company_logo),
        Organizer("Event Organizer", R.drawable.company_logo),
        Organizer("Scout", R.drawable.company_logo),
        Organizer("Eventify", R.drawable.company_logo),
        Organizer("CrewWorks", R.drawable.company_logo),
        Organizer("UniEvents", R.drawable.company_logo),
        Organizer("Voluntrix", R.drawable.company_logo),
        Organizer("Campus Hub", R.drawable.company_logo),
        Organizer("Youth Connect", R.drawable.company_logo),
        Organizer("ProActive", R.drawable.company_logo),
        Organizer("NextGen Events", R.drawable.company_logo),
    )

    val events = listOf(
        Event("Amanah.Co", "Save Turtle", "12 Jan 2026", "Ipoh", R.drawable.location_pic),
        Event("MyHelper", "Help Homeless", "12 Jan 2026", "KL",  R.drawable.location_pic),
        Event("Runner", "Hari Menanam Pokok", "20 Feb 2026", "Genting", R.drawable.location_pic),
        Event("Event Organizer", "Project 2026", "5 Mac 2026", "Penang", R.drawable.location_pic),
        Event("Scout", "Health Awareness", "1 April 2026", "JB", R.drawable.location_pic),
        Event("Event Organizer", "Help Homeless", "15 Mei 2026", "Cyberjaya", R.drawable.location_pic),
        Event("Seaboree", "Beach Cleaning", "1 Jun 2026", "Muar", R.drawable.location_pic),
        Event("Scout", "Bubur Lambuk", "10 Julai 2026", "Subang", R.drawable.location_pic)
    )
    androidx.compose.runtime.LaunchedEffect(shouldReset) {
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
        modifier = Modifier.padding(paddingValues) // Padding Bottom Bar
    ) {

        // --- ROUTE 1: EVENT LIST SCREEN ---
        composable("event_list_screen") {
            var searchState by remember { mutableStateOf(TextFieldValue("")) }

            val filteredEvents = events.filter {
                it.name.contains(searchState.text, ignoreCase = true)
            }

            // LazyColumn searchState dan filteredEvents
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {

                // Home Interface
                item { HeaderTitle("Event list") }
                item { SearchBar(searchState) { searchState = it } }
                item {
                    OrganizerSection(
                        organizers = organizers,
                        onViewAllClick = {
                            homeNavController.navigate("organizers_screen")
                        }
                    )
                }
                item {
                    EventHeader() {
                        homeNavController.navigate("events_screen")
                    }
                }
                item {
                    //
                }


                items(filteredEvents.withIndex().toList()) { (index, event) ->
                    EventCard(event) {
                        homeNavController.navigate("event_details_screen/$index")
                    }
                }

            }

        }


        // --- ROUTE 2 (Event Details) ---
        composable(
            route = "event_details_screen/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: 0
            val selectedEvent = events.getOrNull(eventId) ?: events.first()

            EventDetailsScreen(
                event = selectedEvent,
                onBackClick = {
                    homeNavController.popBackStack()
                },
                onRegisterClick = onRegisterClick
                )
        }

        // --- ROUTE 3: ORGANIZERS  ---
        composable("organizers_screen") {
            OrganizersScreen(
                onBackClick = {
                    homeNavController.popBackStack()
                },
                organizers = organizers
            )
        }

        // --- ROUTE 4: EVENT SCREEN  ---
        composable("events_screen") {
            EventsScreen(
                onBackClick = {
                    homeNavController.popBackStack()
                },
                events = events,
                onEventClick = { clickedEvent ->
                    val index = events.indexOf(clickedEvent)
                    if (index != -1) {
                        homeNavController.navigate("event_details_screen/$index")
                    }
                }
            )
        }
    }
}
