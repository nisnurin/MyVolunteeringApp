package com.example.ict602my_vol

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.ict602my_vol.data.VolEvent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ManageEventViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // Store the listener so we can clean it up later to prevent memory leaks
    private var listenerRegistration: ListenerRegistration? = null

    // Internal list that holds all events from Firestore
    private val _allEvents = mutableStateListOf<VolEvent>()

    // State for the search bar text
    var searchQuery by mutableStateOf("")

    /**
     * Optimized FilteredEvents:
     * Using derivedStateOf ensures that we only re-run the filter logic
     * when the searchQuery or the event list actually changes.
     */
    val filteredEvents: List<VolEvent> by derivedStateOf {
        if (searchQuery.isEmpty()) {
            _allEvents
        } else {
            _allEvents.filter { event ->
                event.name.contains(searchQuery, ignoreCase = true) ||
                        event.location.contains(searchQuery, ignoreCase = true) ||
                        event.organizer.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    init {
        listenToEvents()
    }

    /**
     * Real-time listener for Firestore.
     * Any changes made in the Firebase Console or by Admin will
     * update the UI instantly without refreshing.
     */
    private fun listenToEvents() {
        // orderby("date") helps keep the events organized chronologically
        listenerRegistration = db.collection("events")
            .orderBy("date")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Log error if necessary
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
                                time = doc.getString("time") ?: "", // Critical for your stacked UI
                                location = doc.getString("location") ?: "",
                                description = doc.getString("description") ?: "",
                                imageUrl = doc.getString("imageUrl") ?: ""
                            )
                            _allEvents.add(event)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
    }

    /**
     * Deletes an event from Firestore using its document ID.
     */
    fun deleteEvent(event: VolEvent) {
        db.collection("events").document(event.id).delete()
    }

    /**
     * CLEANUP:
     * This stops the Firestore listener when the user leaves the screen,
     * saving battery and data.
     */
    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}