package com.example.ict602my_vol

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.ict602my_vol.data.VolEvent
import com.google.firebase.firestore.FirebaseFirestore

class ManageEventViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _allEvents = mutableStateListOf<VolEvent>()

    var searchQuery by mutableStateOf("")

    val filteredEvents: List<VolEvent>
        get() = if (searchQuery.isEmpty()) {
            _allEvents
        } else {
            _allEvents.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

    init {
        listenToEvents()
    }

    private fun listenToEvents() {
        db.collection("events").addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            if (snapshot != null) {
                _allEvents.clear()
                for (doc in snapshot.documents) {
                    _allEvents.add(VolEvent(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        organizer = doc.getString("organizer") ?: "General",
                        date = doc.getString("date") ?: "",
                        location = doc.getString("location") ?: "",
                        description = doc.getString("description") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: ""
                    ))
                }
            }
        }
    }

    fun deleteEvent(event: VolEvent) {
        db.collection("events").document(event.id).delete()
    }
}