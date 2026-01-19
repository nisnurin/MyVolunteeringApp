package com.example.ict602my_vol

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import com.example.ict602my_vol.data.VolEvent
// ... (Your existing imports remain the same)
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    private var onGoogleSignInSuccess: (() -> Unit)? = null

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

    private fun signInWithGoogle(onSuccess: () -> Unit) {
        onGoogleSignInSuccess = onSuccess
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Composable
    fun AppRoot() {
        val manageEventViewModel: ManageEventViewModel = viewModel()
        var currentScreen by remember { mutableStateOf("Welcome") }
        var selectedEventForEdit by remember { mutableStateOf<VolEvent?>(null) }
        var loginRole by remember { mutableStateOf("Volunteer") }



        when (currentScreen) {
            "Welcome" -> WelcomeScreen(onGetStarted = { currentScreen = "Main" })

            "Main" -> MainScreen(
                onSignUpSuccess = {
                    if (loginRole == "Admin") {
                        currentScreen = "AdminDashboard"
                    } else {
                        currentScreen = "Home"
                    }
                },
                onNavigateToLogin = { role ->
                    loginRole = role
                    currentScreen = if (role == "Admin") "AdminLogin" else "VolunteerLogin"
                },
                onGoogleClick = { signInWithGoogle {
                    loginRole = "Volunteer"
                    currentScreen = "Home"
                } }
            )
            "VolunteerLogin" -> LoginScreen(
                onLoginSuccess = {
                    loginRole = "Volunteer"
                    currentScreen = "Home"
                },
                onBackToSignUp = {
                    currentScreen = "Main"
                }
            )
            // --- ADMIN AUTH SECTION ---
            "AdminLogin" -> AdminLoginScreen(
                onLoginSuccess = {
                    loginRole = "Admin"
                    currentScreen = "AdminDashboard"
                },
                onNavigateToSignUp = {
                    currentScreen = "AdminSignUp" }
            )

            "AdminSignUp" -> AdminScreen( // This is your Admin signup screen
                onContinueSuccess = {
                    loginRole = "Admin"
                    currentScreen = "AdminDashboard"
                },
                onNavigateToLogin = { currentScreen = "AdminLogin" },
                onGoogleClick = { signInWithGoogle {
                    loginRole = "Admin"
                    currentScreen = "AdminDashboard"
                } }
            )

            // --- ADMIN MAIN SECTION ---
            "AdminDashboard" -> AdminDashboardScreen(
                onManageEventClick = { currentScreen = "ManageEvent" },
                onViewReportClick = { currentScreen = "Report" },
                onProfileClick = { currentScreen = "AdminProfile" },
                onLogout = {
                    auth.signOut()
                    currentScreen = "Welcome"
                }
            )

            "AdminProfile" -> AdminProfileScreen(
                onHomeClick = { currentScreen = "AdminDashboard" },
                onLogout = {
                    auth.signOut()
                    currentScreen = "Welcome"
                }
            )

            "ManageEvent" -> ManageEventScreen(
                viewModel = manageEventViewModel,
                onAddEventClick = {
                    selectedEventForEdit = null
                    currentScreen = "AddEvent"
                },
                onEditEventClick = { event ->
                    selectedEventForEdit = event
                    currentScreen = "AddEvent"
                },
                onReportClick = { currentScreen = "Report" },
                onBackClick = { currentScreen = "AdminDashboard" }
            )

            "AddEvent" -> AddEventScreen(
                onNavigateBack = { currentScreen = "ManageEvent" },
                eventToEdit = selectedEventForEdit
            )

            "Report" -> ReportScreen(
                viewModel = manageEventViewModel,
                onBackClick = { currentScreen = "ManageEvent" }
            )

            // --- VOLUNTEER SECTION ---
            "Home" -> HomePage(
                manageEventViewModel = manageEventViewModel,
                onManageClick = { /* Admins only, but kept for logic */ },
                onLogout = {
                    auth.signOut()
                    googleSignInClient.signOut()
                    currentScreen = "Welcome"
                }
            )
        }
    }

    // ... [Keep your existing HomePage and onActivityResult code exactly as they were]
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
            Box(modifier = Modifier.
            fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
            )
            {
                when (selectedTab) {
                    0 -> HomeScreen(
                        paddingValues = innerPadding,
                        viewModel = manageEventViewModel,
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
                    3 -> ActivityScreen(
                        padding = innerPadding,
                        viewModel = manageEventViewModel,
                        onNavigateToProfile = { selectedTab = 2 },
                        onLogout = onLogout
                    )
                }

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

                        // NI PENTING: Supaya skrin bertukar ke AdminDashboard
                        runOnUiThread {
                            onGoogleSignInSuccess?.invoke()
                        }
                    }
                }
            } catch (e: ApiException) {
                e.printStackTrace()
                Toast.makeText(this, "Google Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
