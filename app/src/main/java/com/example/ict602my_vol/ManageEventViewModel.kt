package com.example.ict602my_vol

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.ict602my_vol.data.VolEvent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ManageEventViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // Using a private state list to manage internal changes
    private val _allEvents = mutableStateListOf<VolEvent>()

    // State for the search bar
    var searchQuery by mutableStateOf("")

    /**
     * Updated FilteredEvents:
     * Now searches through Name, Location, and Organizer for a better user experience.
     */
    val filteredEvents: List<VolEvent>
        get() = if (searchQuery.isEmpty()) {
            _allEvents
        } else {
            _allEvents.filter { event ->
                event.name.contains(searchQuery, ignoreCase = true) ||
                        event.location.contains(searchQuery, ignoreCase = true) ||
                        event.organizer.contains(searchQuery, ignoreCase = true)
            }
        }

    init {
        listenToEvents()
    }

    /**
     * Real-time listener:
     * Automatically updates the UI whenever data changes in Firestore.
     */
    private fun listenToEvents() {
        // We order by date so the newest/closest events appear in a logical order
        db.collection("events")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Log error here if you have a logging system
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _allEvents.clear()
                    for (doc in snapshot.documents) {
                        try {
                            val event = VolEvent(
                                id = doc.id,
                                name = doc.getString("name") ?: "",
                                organizer = doc.getString("organizer") ?: "General",
                                date = doc.getString("date") ?: "",
                                // FULLY SYNCED: Pulling 'time' from Firestore
                                time = doc.getString("time") ?: "",
                                location = doc.getString("location") ?: "",
                                description = doc.getString("description") ?: "",
                                imageUrl = doc.getString("imageUrl") ?: ""
                            )
                            _allEvents.add(event)
                        } catch (e: Exception) {
                            // Prevents the app from crashing if one document has bad data
                            e.printStackTrace()
                        }
                    }
                }
            }
    }

    /**
     * Delete function:
     * Removes the event from Firestore. The SnapshotListener above
     * will automatically remove it from the list once the deletion is confirmed.
     */
    fun deleteEvent(event: VolEvent) {
        db.collection("events").document(event.id).delete()
            .addOnFailureListener {
                // Potential for a Toast or error message here
            }
    }
}