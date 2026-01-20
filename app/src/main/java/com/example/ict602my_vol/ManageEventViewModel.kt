package com.example.ict602my_vol

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.ict602my_vol.data.VolEvent
import com.example.ict602my_vol.data.Organizer
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ManageEventViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private var eventListener: ListenerRegistration? = null
    private var registrationListener: ListenerRegistration? = null
    private var organizerListener: ListenerRegistration? = null

    private val _allEvents = mutableStateListOf<VolEvent>()
    private val _registrations = mutableStateListOf<RegistrationRecord>()
    private val _organizers = mutableStateListOf<Organizer>()

    val allEvents: List<VolEvent> = _allEvents
    val registrations: List<RegistrationRecord> = _registrations
    val organizers: List<Organizer> = _organizers
    var searchQuery by mutableStateOf("")

    val filteredEvents: List<VolEvent> by derivedStateOf {
        if (searchQuery.isEmpty()) _allEvents
        else _allEvents.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    init {
        listenToEvents()
        listenToRegistrations()
        listenToOrganizers()
    }

    private fun listenToOrganizers() {
        organizerListener = db.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    _organizers.clear()
                    for (doc in snapshot.documents) {
                        val name = doc.getString("fullName") ?: "Unknown"
                        val imageEncoded = doc.getString("profileImageEncoded")
                        val finalImage = if (!imageEncoded.isNullOrEmpty()) "data:image/jpeg;base64,$imageEncoded" else ""
                        
                        if (name.isNotEmpty()) {
                            _organizers.add(Organizer(name, finalImage))
                        }
                    }
                }
            }
    }

    private fun listenToEvents() {
        eventListener = db.collection("events").orderBy("date")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    _allEvents.clear()
                    for (doc in snapshot.documents) {
                        _allEvents.add(VolEvent(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            organizer = doc.getString("organizer") ?: "",
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

    private fun listenToRegistrations() {
        // Listening to the 'registrations' collection populated by RegisterScreen
        registrationListener = db.collection("registrations")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    _registrations.clear()
                    for (doc in snapshot.documents) {
                        _registrations.add(RegistrationRecord(
                            id = doc.id,
                            userName = doc.getString("userName") ?: "Unknown",
                            userEmail = doc.getString("userEmail") ?: "",
                            eventName = doc.getString("eventName") ?: "No Event",
                            eventId = doc.getString("eventId") ?: ""
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
        organizerListener?.remove()
    }
}