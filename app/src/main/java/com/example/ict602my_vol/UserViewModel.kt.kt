package com.example.ict602my_vol

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserViewModel : ViewModel() {
    // This variable holds the "Source of Truth" for the image
    var selectedImageUri by mutableStateOf<Uri?>(null)
    
    // User details with default "Loading" state
    var userName by mutableStateOf("Loading...")
    var userEmail by mutableStateOf("Loading...")
    var userNationality by mutableStateOf("Loading...")

    private val authStateListener = com.google.firebase.auth.FirebaseAuth.AuthStateListener { 
        fetchUserData()
    }

    init {
        com.google.firebase.auth.FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
        fetchUserData()
    }

    override fun onCleared() {
        super.onCleared()
        com.google.firebase.auth.FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }

    fun fetchUserData() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            // 1. Basic Auth Data (Fallback if Firestore empty)
            userEmail = user.email ?: "No Email"
            userName = user.displayName ?: "Volunteer"
            
            // 2. Fetch from Firestore (users collection)
            val db = Firebase.firestore
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val firestoreName = document.getString("fullName")
                        val firestoreNationality = document.getString("nationality")
                        
                        // If Firestore has data, override the Auth data
                        if (!firestoreName.isNullOrEmpty()) {
                            userName = firestoreName
                        }
                        
                        if (!firestoreNationality.isNullOrEmpty()) {
                            userNationality = firestoreNationality
                        } else {
                            userNationality = "Unknown Location"
                        }
                    } else {
                        // Document doesn't exist (maybe Google Sign In without extra data)
                        if (userNationality == "Loading...") userNationality = "Unknown Location"
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("UserViewModel", "Error fetching user data", e)
                    if (userName == "Loading...") userName = "Guest"
                    if (userNationality == "Loading...") userNationality = "Unknown"
                }
        } else {
            userName = "Guest"
            userEmail = "Not Logged In"
            userNationality = "-"
        }
    }
}