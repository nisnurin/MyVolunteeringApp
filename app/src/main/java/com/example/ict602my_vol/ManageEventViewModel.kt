package com.example.ict602my_vol

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// 1. Data structure for each Event item
data class VolEvent(
    val id: Int,
    val name: String,
    val organizer: String,
    val date: String,
    val location: String
)

// 2. The ViewModel to handle the data and search logic
class ManageEventViewModel : ViewModel() {

    // This is the source list of events (simulating your data)
    private val _allEvents = mutableStateListOf(
        VolEvent(1, "Summer Radish Festival", "Local Farm Org", "Oct 25, 2025", "Central Park"),
        VolEvent(2, "Tech Meetup 2025", "Dev Community", "Nov 01, 2025", "Convention Center"),
        VolEvent(3, "Art Workshop", "Creative Minds", "Nov 15, 2025", "City Studio"),
        VolEvent(4, "Beach Cleanup", "Eco-Warriors", "Dec 05, 2025", "Port Dickson")
    )

    // This holds the text typed into the search bar
    var searchQuery by mutableStateOf("")

    // This is the list that actually displays (filtered by search)
    val filteredEvents: List<VolEvent>
        get() = if (searchQuery.isEmpty()) {
            _allEvents
        } else {
            _allEvents.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

    // Function to delete an event
    fun deleteEvent(event: VolEvent) {
        _allEvents.remove(event)
    }
}