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
import com.example.ict602my_vol.data.Event as VolEvent
import com.example.ict602my_vol.ui.screens.MainScreen
import com.example.ict602my_vol.ui.screens.AdminLoginScreen

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
        // FIX: Gunakan VolEvent? supaya match dengan state
        var selectedEventForEdit by remember { mutableStateOf<VolEvent?>(null) }

        when (currentScreen) {
            "Welcome" -> WelcomeScreen(onGetStarted = { currentScreen = "Main" })

            "Main" -> MainScreen(
                onGoogleClick = { signInWithGoogle() },
                onSignUpSuccess = { currentScreen = "Home" },
                onNavigateToLogin = { currentScreen = "AdminLogin" }
            )
            "AdminLogin" -> AdminLoginScreen(
                onLoginSuccess = {
                    currentScreen = "Home" // Kalau nak hantar ke Dashboard Admin, tukar "AdminDashboard"
                },
                onNavigateToSignUp = {
                    currentScreen = "Main" // Patah balik ke Sign Up
                }
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
                onEditEventClick = { event ->  // Guna 'it' atau tukar nama kat sini
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
        var registrationData by remember { mutableStateOf(RegistrationData()) }

        Box(modifier = Modifier.fillMaxSize()) {

            Scaffold(
                bottomBar = {
                    if (currentSubScreen == "Main") {
                        BottomNavigationBar(
                            selected = selectedTab,
                            onSelect = { selectedTab = it }
                        )
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier) {
                    when (selectedTab) {
                        0 -> HomeScreen(paddingValues = innerPadding, onRegisterClick = { currentSubScreen = "Register" })
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

            // Overlay Skrin Pendaftaran (Tutup Benda Putih)
            if (currentSubScreen != "Main") {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    when (currentSubScreen) {
                        "Register" -> RegisterScreen(
                            onBack = { currentSubScreen = "Main" },
                            onRegisterSuccess = { data ->
                                registrationData = data
                                currentSubScreen = "Success"
                            }
                        )
                        "Success" -> SuccessScreen(
                            eventName = registrationData.eventName,
                            onViewRegistration = { currentSubScreen = "View" },
                            onBackToHome = {
                                selectedTab = 0
                                currentSubScreen = "Main"
                            }
                        )
                        "View" -> ViewRegistrationScreen(
                            data = registrationData,
                            onBackToHome = {
                                currentSubScreen = "Main"
                                selectedTab = 0
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