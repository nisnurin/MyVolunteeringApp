package com.example.ict602my_vol

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserViewModel : ViewModel() {
    // This variable holds the "Source of Truth" for the image
    var selectedImageUri by mutableStateOf<Uri?>(null)

    // User details with default "Loading" state
    var userName by mutableStateOf("Loading...")
    var userEmail by mutableStateOf("Loading...")
    var userNationality by mutableStateOf("Loading...")
    var userAboutMe by mutableStateOf("Passionate volunteer currently exploring new social activities.")
    var userPhone by mutableStateOf("-")
    var userGender by mutableStateOf("-")
    var userStory by mutableStateOf("I love volunteering!")
    var profileImageEncoded by mutableStateOf("")

    // Stats
    var registeredEventsCount by mutableIntStateOf(0)
    var availableEventsCount by mutableIntStateOf(0)

    // Publicly exposed valid event data for filtering in other screens (e.g., NotificationScreen)
    var validEventIds by mutableStateOf(setOf<String>())
    var validEventNames by mutableStateOf(setOf<String>())

    // Role
    var isAdmin by mutableStateOf(false)

    // Admin Dashboard Stats
    var adminEventCount by mutableIntStateOf(0)
    var adminUserCount by mutableIntStateOf(0)

    private val authStateListener = FirebaseAuth.AuthStateListener { fetchUserData() }
    
    private var eventsListener: ListenerRegistration? = null
    private var registrationsListener: ListenerRegistration? = null
    private var adminStatsEventsListener: ListenerRegistration? = null
    private var adminStatsUsersListener: ListenerRegistration? = null

    // Local data structures for robust counting
    private data class EventMinimal(val id: String, val name: String)
    private data class RegistrationMinimal(val eventId: String, val eventName: String)

    private var allEvents = listOf<EventMinimal>()
    private var myRegistrations = listOf<RegistrationMinimal>()

    init {
        Firebase.auth.addAuthStateListener(authStateListener)
        fetchUserData()
        listenToAvailableEvents()
        listenToAdminDashboardStats()
    }

    override fun onCleared() {
        super.onCleared()
        Firebase.auth.removeAuthStateListener(authStateListener)
        eventsListener?.remove()
        registrationsListener?.remove()
        adminStatsEventsListener?.remove()
        adminStatsUsersListener?.remove()
    }
    
    private fun listenToAdminDashboardStats() {
        val db = Firebase.firestore
        adminStatsEventsListener = db.collection("events")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) adminEventCount = snapshot.size()
            }
        adminStatsUsersListener = db.collection("users")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) adminUserCount = snapshot.size()
            }
    }
    
    fun fetchUserData() {
        val user = Firebase.auth.currentUser
        if (user == null) {
            // User is signed out, reset to guest state
            userName = "Guest"
            userEmail = "Not Logged In"
            userNationality = "-"
            isAdmin = false
            // selectedImageUri = null // Keep image to prevent flickering/loss on logout
            registeredEventsCount = 0
            registrationsListener?.remove()
            registrationsListener = null
            myRegistrations = emptyList()
            // profileImageEncoded = "" // Keep encoded string
            return
        }

        // User is signed in. Set initial data from auth profile.
        listenToUserRegistrations(user.email)
        userEmail = user.email ?: "No Email"
        userName = user.displayName ?: "Volunteer"
        val db = Firebase.firestore

        // Step 1: Check 'volunteers' collection first for detailed info
        db.collection("volunteers").document(user.uid).get()
            .addOnSuccessListener { volunteerDoc ->
                if (volunteerDoc != null && volunteerDoc.exists()) {
                    // Data found in 'volunteers', this is the primary source of truth
                    val vName = volunteerDoc.getString("fullName")
                    val vNationality = volunteerDoc.getString("nationality")
                    val vAbout = volunteerDoc.getString("aboutMe")
                    val vImage = volunteerDoc.getString("profileImageUri")
                    val vEmergencyPhone = volunteerDoc.getString("emergencyContactPhone")
                    val vPhone = volunteerDoc.getString("phone")
                    val vGender = volunteerDoc.getString("gender")
                    val vStory = volunteerDoc.getString("story")

                    if (!vName.isNullOrEmpty()) userName = vName
                    if (!vNationality.isNullOrEmpty()) userNationality = vNationality
                    if (!vAbout.isNullOrEmpty()) userAboutMe = vAbout
                    if (!vImage.isNullOrEmpty()) selectedImageUri = Uri.parse(vImage)
                    if (!vStory.isNullOrEmpty()) userStory = vStory
                    
                    // Prioritize phone (signup phone) if available, otherwise fallback or keep default
                    if (!vPhone.isNullOrEmpty()) {
                        userPhone = vPhone
                    } else if (!vEmergencyPhone.isNullOrEmpty()) {
                        userPhone = vEmergencyPhone
                    }
                    
                    if (!vGender.isNullOrEmpty()) userGender = vGender
                } else {
                    // Step 2: Not in 'volunteers', check 'users' (for admins or basic users)
                    db.collection("users").document(user.uid).get()
                        .addOnSuccessListener { userDoc ->
                            if (userDoc != null && userDoc.exists()) {
                                if (userDoc.getString("role") == "admin") isAdmin = true

                                val firestoreName = userDoc.getString("fullName")
                                val firestoreNationality = userDoc.getString("nationality")
                                val firestorePhone = userDoc.getString("phoneNumber") ?: userDoc.getString("phone")
                                val firestoreImageEncoded = userDoc.getString("profileImageEncoded") ?: ""

                                if (!firestoreName.isNullOrEmpty()) userName = firestoreName
                                if (!firestoreNationality.isNullOrEmpty()) userNationality = firestoreNationality
                                if (!firestorePhone.isNullOrEmpty()) userPhone = firestorePhone
                                profileImageEncoded = firestoreImageEncoded
                                
                                if (firestoreImageEncoded.isNotEmpty()) {
                                    selectedImageUri = Uri.parse("data:image/jpeg;base64,$firestoreImageEncoded")
                                } else {
                                    selectedImageUri = null
                                }
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserViewModel", "Error fetching user data from Firestore", e)
                // Fallback to auth data if Firestore fails
                if (userNationality == "Loading...") userNationality = "Unknown Location"
            }
    }

    private fun listenToAvailableEvents() {
        val db = Firebase.firestore
        eventsListener = db.collection("events").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                availableEventsCount = snapshot.size()
                // Store minimal event data for cross-referencing
                val events = snapshot.documents.map { doc ->
                    EventMinimal(
                        id = doc.id,
                        name = doc.getString("name") ?: ""
                    )
                }
                allEvents = events
                
                // Update public states
                validEventIds = events.map { it.id }.toSet()
                validEventNames = events.map { it.name }.toSet()
                
                recalculateRegisteredCount()
            }
        }
    }

    private fun listenToUserRegistrations(email: String?) {
        registrationsListener?.remove()
        if (email == null) {
            registeredEventsCount = 0
            myRegistrations = emptyList()
            return
        }

        val db = Firebase.firestore
        registrationsListener = db.collection("registrations")
            .whereEqualTo("userEmail", email)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    // Store minimal registration data
                    myRegistrations = snapshot.documents.map { doc ->
                        RegistrationMinimal(
                            eventId = doc.getString("eventId") ?: "",
                            eventName = doc.getString("eventName") ?: ""
                        )
                    }
                    recalculateRegisteredCount()
                }
            }
    }

    private fun recalculateRegisteredCount() {
        // Exact logic from ActivityScreen:
        // 1. Get IDs and Names from registrations
        val myEventIds = myRegistrations.map { it.eventId }.filter { it.isNotEmpty() }.toSet()
        val myEventNames = myRegistrations.map { it.eventName }.toSet()

        // 2. Filter ALL events to find matches by ID OR Name
        val count = allEvents.count { event ->
            event.id in myEventIds || event.name in myEventNames
        }
        
        registeredEventsCount = count
    }

    fun updateUserProfile(newName: String, newNationality: String, newAbout: String, onResult: (Boolean) -> Unit) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            val db = Firebase.firestore
            val updates = hashMapOf<String, Any>(
                "fullName" to newName,
                "nationality" to newNationality,
                "aboutMe" to newAbout
            )

            // Update in volunteers collection (primary for volunteers)
            db.collection("volunteers").document(user.uid)
                .set(updates, SetOptions.merge()) // Use set with merge to create or update
                .addOnSuccessListener {
                    userName = newName
                    userNationality = newNationality
                    userAboutMe = newAbout
                    onResult(true)
                }
                .addOnFailureListener { onResult(false) }
        }
    }

    fun updateUserStory(newStory: String, onResult: (Boolean) -> Unit) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            val db = Firebase.firestore
            val updates = hashMapOf<String, Any>(
                "story" to newStory
            )
            db.collection("volunteers").document(user.uid)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener {
                    userStory = newStory
                    onResult(true)
                }
                .addOnFailureListener { onResult(false) }
        } else {
            onResult(false)
        }
    }

    fun updateUserDetails(newPhone: String, newGender: String, onResult: (Boolean) -> Unit) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            val db = Firebase.firestore
            val updates = hashMapOf<String, Any>(
                "phone" to newPhone, // Update the main phone field
                "emergencyContactPhone" to newPhone, // Optionally keep this in sync or remove if distinct
                "gender" to newGender
            )
            db.collection("volunteers").document(user.uid)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener {
                    userPhone = newPhone
                    userGender = newGender
                    onResult(true)
                }
                .addOnFailureListener { onResult(false) }
        } else {
            onResult(false)
        }
    }

    fun saveProfileImage(uri: Uri) {
        selectedImageUri = uri
        val user = Firebase.auth.currentUser
        if (user != null) {
            val db = Firebase.firestore
            val updates = hashMapOf<String, Any>(
                "profileImageUri" to uri.toString()
            )
            db.collection("volunteers").document(user.uid)
                .set(updates, SetOptions.merge())
        }
    }

    fun saveAdminProfileImage(uri: Uri, base64: String) {
        selectedImageUri = uri
        profileImageEncoded = base64
        val user = Firebase.auth.currentUser
        if (user != null) {
            val db = Firebase.firestore
            db.collection("users").document(user.uid)
                .update("profileImageEncoded", base64)
        }
    }
}
