package com.example.ict602my_vol

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.ict602my_vol.data.VolEvent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

// Ensure RegistrationRecord is accessible


class ManageEventViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private var eventListener: ListenerRegistration? = null
    private var registrationListener: ListenerRegistration? = null

    private val _allEvents = mutableStateListOf<VolEvent>()
    private val _registrations = mutableStateListOf<RegistrationRecord>()

    val registrations: List<RegistrationRecord> = _registrations
    var searchQuery by mutableStateOf("")

    /**
     * UPDATED SEARCH LOGIC:
     * Filters by both 'name' and 'location' to match the search bar's
     * expected behavior on the Home and Manage screens.
     */
    val filteredEvents: List<VolEvent> by derivedStateOf {
        if (searchQuery.isEmpty()) _allEvents
        else _allEvents.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.location.contains(searchQuery, ignoreCase = true)
        }
    }

    init {
        listenToEvents()
        listenToRegistrations()
    }

    private fun listenToEvents() {
        eventListener = db.collection("events").orderBy("date")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    _allEvents.clear()
                    for (doc in snapshot.documents) {
                        _allEvents.add(VolEvent(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            // Use "Organizer Name" as a fallback for the teal card header
                            organizer = doc.getString("organizer") ?: "General Organizer",
                            date = doc.getString("date") ?: "",
                            time = doc.getString("time") ?: "",
                            location = doc.getString("location") ?: "",
                            description = doc.getString("description") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: ""
                        ))
                    }
                }
            }
    }

    fun listenToRegistrations() {
        registrationListener = db.collection("registrations")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    _registrations.clear()
                    for (doc in snapshot.documents) {
                        _registrations.add(RegistrationRecord(
                            id = doc.id,
                            userName = doc.getString("userName") ?: "Unknown",
                            userEmail = doc.getString("userEmail") ?: "",
                            eventName = doc.getString("eventName") ?: "No Event"
                        ))
                    }
                }
            }
    }

    fun deleteEvent(event: VolEvent) {
        db.collection("events").document(event.id).delete()
    }

    override fun onCleared() {
        super.onCleared()
        eventListener?.remove()
        registrationListener?.remove()
    }
}