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
import com.example.ict602my_vol.UserViewModel

// Standardizing the alias to match the HomeComponents/HomeScreen logic
import com.example.ict602my_vol.data.VolEvent

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
        // Shared ViewModel for all screens
        val manageEventViewModel: ManageEventViewModel = viewModel()
        var currentScreen by remember { mutableStateOf("Welcome") }
        var selectedEventForEdit by remember { mutableStateOf<VolEvent?>(null) }
        var loginRole by remember { mutableStateOf("Volunteer") }

        when (currentScreen) {
            "Welcome" -> WelcomeScreen(onGetStarted = { currentScreen = "Main" })
            "Main" -> MainScreen(
                onSignUpSuccess = { currentScreen = "Home" },
                onNavigateToLogin = { currentScreen = "LoginChoice" },
                onGoogleClick = { signInWithGoogle() }
            )
            "LoginChoice" -> {
                if (loginRole == "Admin") {
                    AdminLoginScreen(
                        onLoginSuccess = {
                            loginRole = "Admin"
                            currentScreen = "AdminDashboard"
                        },
                        onNavigateToSignUp = { currentScreen = "Main" }
                    )
                } else {
                    LoginScreen(
                        onLoginSuccess = { currentScreen = "Home" },
                        onBackToSignUp = { currentScreen = "Main" }
                    )
                }
            }

            "AdminDashboard" -> AdminDashboardScreen(
                onManageEventClick = { currentScreen = "ManageEvent" },
                onViewReportClick = {
                    Toast.makeText(this@MainActivity, "Opening Reports...", Toast.LENGTH_SHORT).show()
                },
                onLogout = { currentScreen = "Welcome" }
            )

            "Home" -> HomePage(
                manageEventViewModel = manageEventViewModel, // Pass shared VM
                onManageClick = { currentScreen = "ManageEvent" },
                onLogout = {
                    auth.signOut()
                    googleSignInClient.signOut()
                    currentScreen = "Welcome"
                }
            )

            "ManageEvent" -> ManageEventScreen(
                viewModel = manageEventViewModel,
                onBackClick = {
                    currentScreen = if (loginRole == "Admin") "AdminDashboard" else "Home"
                },
                onAddEventClick = {
                    selectedEventForEdit = null
                    currentScreen = "AddEvent"
                },
                onEditEventClick = { event ->
                    selectedEventForEdit = event
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
        manageEventViewModel: ManageEventViewModel,
        onManageClick: () -> Unit,
        onLogout: () -> Unit,
        userViewModel: UserViewModel = viewModel()
    ) {
        var selectedTab by remember { mutableStateOf(0) }
        var currentSubScreen by remember { mutableStateOf("Main") }
        var registrationData by remember { mutableStateOf(RegistrationData()) }
        var selectedEventForRegistration by remember { mutableStateOf<VolEvent?>(null) }
        var resetHome by remember { mutableStateOf(false) }

        Scaffold(
            bottomBar = {
                if (currentSubScreen == "Main") {
                    BottomNavigationBar(selected = selectedTab, onSelect = { selectedTab = it })
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                when (selectedTab) {
                    0 -> HomeScreen(
                        paddingValues = innerPadding,
                        viewModel = manageEventViewModel, // Pass VM to HomeScreen
                        onRegisterClick = { event ->
                            selectedEventForRegistration = event
                            currentSubScreen = "Register"
                        },
                        shouldReset = resetHome,
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

                // Sub-screens for Registration Flow
                if (currentSubScreen != "Main") {
                    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                        when (currentSubScreen) {
                            "Register" -> {
                                selectedEventForRegistration?.let { event ->
                                    RegisterScreen(
                                        event = event,
                                        onBack = {
                                            currentSubScreen = "Main"
                                            selectedEventForRegistration = null
                                        },
                                        onRegisterSuccess = { data ->
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
    }

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