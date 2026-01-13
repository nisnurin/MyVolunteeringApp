package com.example.ict602my_vol

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

// 1. Updated Data Structure - id MUST be String for Firebase Auto-IDs
data class VolEvent(
    val id: String = "",
    val name: String = "",
    val organizer: String = "",
    val date: String = "",
    val location: String = ""
)

class ManageEventViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // This list stays synced with Firebase
    private val _allEvents = mutableStateListOf<VolEvent>()

    // Search query state for the search bar
    var searchQuery by mutableStateOf("")

    // The list that the UI observes (LazyColumn items)
    val filteredEvents: List<VolEvent>
        get() = if (searchQuery.isEmpty()) {
            _allEvents
        } else {
            _allEvents.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

    init {
        // Automatically start fetching data when the ViewModel is created
        listenToEvents()
    }

    /**
     * Real-time listener: This will trigger every time a document is
     * Added, Deleted, or Modified in the 'events' collection.
     */
    private fun listenToEvents() {
        db.collection("events").addSnapshotListener { snapshot, error ->
            if (error != null) {
                // You can log error here if needed
                return@addSnapshotListener
            }

            if (snapshot != null) {
                _allEvents.clear()
                for (doc in snapshot.documents) {
                    val event = VolEvent(
                        id = doc.id, // The unique random ID from Firebase
                        name = doc.getString("name") ?: "",
                        organizer = doc.getString("organizer") ?: "General",
                        date = doc.getString("date") ?: "",
                        location = doc.getString("location") ?: ""
                    )
                    _allEvents.add(event)
                }
            }
        }
    }

    /**
     * Delete an event from the Cloud Firestore database
     */
    fun deleteEvent(event: VolEvent) {
        db.collection("events").document(event.id).delete()
            .addOnSuccessListener {
                // Success - The SnapshotListener will automatically handle UI removal
            }
            .addOnFailureListener {
                // Handle deletion error
            }
    }
}