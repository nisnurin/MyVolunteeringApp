package com.example.ict602my_vol

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ict602my_vol.data.RegistrationData
import com.example.ict602my_vol.ui.BottomNavigationBar
import com.example.ict602my_vol.ui.home.*
import com.example.ict602my_vol.ui.screens.*
import com.example.ict602my_vol.ui.theme.EventTest3Theme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

// /// ALIAS FOR THE EVENT DATA CLASS TO AVOID CONFLICTS ///
import com.example.ict602my_vol.data.Event as VolEvent

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            EventTest3Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    AppRoot()
                }
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Composable
    fun AppRoot() {
        val manageEventViewModel: ManageEventViewModel = viewModel()
        var currentScreen by remember { mutableStateOf("Welcome") }
        var selectedEventForEdit by remember { mutableStateOf<VolEvent?>(null) }

        when (currentScreen) {
            "Welcome" -> WelcomeScreen(onGetStarted = { currentScreen = "Main" })
            "Main" -> MainScreen(
                onGoogleClick = { signInWithGoogle() },
                onSignUpSuccess = { currentScreen = "Home" },
                onNavigateToLogin = { currentScreen = "Login" }
            )
            "Login" -> LoginScreen(
                onLoginSuccess = { currentScreen = "Home" },
                onBackToSignUp = { currentScreen = "Main" }
            )
            "Home" -> HomePage(
                onManageClick = { currentScreen = "ManageEvent" },
                onLogout = {
                    auth.signOut()
                    googleSignInClient.signOut()
                    currentScreen = "Welcome"
                }
            )
            "ManageEvent" -> ManageEventScreen(
                viewModel = manageEventViewModel,
                onBackClick = { currentScreen = "Home" },
                onAddEventClick = {
                    selectedEventForEdit = null
                    currentScreen = "AddEvent"
                },
                onEditEventClick = { event ->
                    selectedEventForEdit = event as VolEvent
                    currentScreen = "AddEvent"
                }
            )
            "AddEvent" -> AddEventScreen(
                onNavigateBack = { currentScreen = "ManageEvent" },
                eventToEdit = selectedEventForEdit
            )
        }
    }

    @Composable
    fun HomePage(
        onManageClick: () -> Unit,
        onLogout: () -> Unit,
        userViewModel: UserViewModel = viewModel()
    ) {
        var selectedTab by remember { mutableStateOf(0) }
        var currentSubScreen by remember { mutableStateOf("Main") }

        // /// STATE TO STORE REGISTRATION DETAILS FOR THE VIEW REGISTRATION SCREEN ///
        var registrationData by remember { mutableStateOf(RegistrationData()) }

        // /// STATE TO STORE THE SPECIFIC EVENT SELECTED BY THE USER ///
        var selectedEventForRegistration by remember { mutableStateOf<VolEvent?>(null) }

        // ... (State declarations remain the same)
        var resetHome by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                bottomBar = {
                    if (currentSubScreen == "Main") {
                        BottomNavigationBar(selected = selectedTab, onSelect = { selectedTab = it })
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize()) {
                    // Update the call to HomeScreen
                    when (selectedTab) {
                        0 -> HomeScreen(
                            paddingValues = innerPadding,
                            onRegisterClick = { event ->
                                selectedEventForRegistration = event
                                currentSubScreen = "Register"
                            },
                            // Pass the reset flag to your HomeScreen
                            shouldReset = resetHome,
                            // Provide a lambda to reset the flag after navigation is complete
                            onResetComplete = { resetHome = false }
                        )

                    1 -> NotificationScreen(innerPadding, userViewModel)
                        2 -> ProfileScreen(
                            padding = innerPadding,
                            userViewModel = userViewModel,
                            onNavigateToActivities = { selectedTab = 3 },
                            onManageEventsClick = onManageClick,
                            onLogout = onLogout
                        )
                        3 -> ActivityScreen(padding = innerPadding, onNavigateToProfile = { selectedTab = 2 })
                    }
                }
            }

            // /// OVERLAY SCREENS SECTION ///
            if (currentSubScreen != "Main") {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    when (currentSubScreen) {
                        "Register" -> {
                            // /// PASS THE SELECTED EVENT TO THE REGISTER SCREEN ///
                            selectedEventForRegistration?.let { event ->
                                RegisterScreen(
                                    event = event,
                                    onBack = {
                                        currentSubScreen = "Main"
                                        selectedEventForRegistration = null
                                    },
                                    onRegisterSuccess = { data ->
                                        // /// SAVE THE DATA SO IT CAN BE VIEWED LATER ///
                                        registrationData = data
                                        currentSubScreen = "Success"
                                    }
                                )
                            }
                        }
                        "Success" -> SuccessScreen(
                            eventName = registrationData.eventName,
                            onViewRegistration = { currentSubScreen = "View" },
                            onBackToHome = {
                                resetHome = true
                                selectedTab = 0
                                currentSubScreen = "Main"
                                selectedEventForRegistration = null
                            }
                        )
                        "View" -> ViewRegistrationScreen(
                            // /// PASS REGISTRATION DATA TO THE SUMMARY VIEW SCREEN ///
                            data = registrationData,
                            onBackToHome = {
                                currentSubScreen = "Main"
                                selectedTab = 0
                                selectedEventForRegistration = null
                            }
                        )
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener(this) { authTask ->
                    if (authTask.isSuccessful) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: ApiException) { e.printStackTrace() }
        }
    }
}
