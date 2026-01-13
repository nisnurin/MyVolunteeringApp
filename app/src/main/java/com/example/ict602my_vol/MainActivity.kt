package com.example.ict602my_vol

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ict602my_vol.ui.screens.MainScreen
import com.example.ict602my_vol.ui.screens.WelcomeScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ict602my_vol.ui.home.HomeScreen
import com.example.ict602my_vol.ui.BottomNavigationBar
import com.example.ict602my_vol.ui.theme.EventTest3Theme
import com.example.ict602my_vol.ui.screens.RegisterScreen
import com.example.ict602my_vol.ui.screens.SuccessScreen
import com.example.ict602my_vol.ui.screens.ViewRegistrationScreen
import com.example.ict602my_vol.ui.screens.LoginScreen
import com.example.ict602my_vol.data.RegistrationData
import com.example.ict602my_vol.data.Event

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        // Pastikan default_web_client_id wujud dalam strings.xml anda
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            EventTest3Theme {
                AppRoot()
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Composable
    fun AppRoot() {
        // Aliran utama: Mula dengan WelcomeScreen
        var currentScreen by remember { mutableStateOf("Welcome") }

        when (currentScreen) {
            "Welcome" -> WelcomeScreen(
                onGetStarted = {
                    // Apabila klik 'Get Started', terus ke MainScreen yang ada tab Volunteer/Admin
                    currentScreen = "Main"
                }
            )

            "Main" -> MainScreen(
                onSignUpSuccess = {
                    currentScreen = "Home"
                },
                onNavigateToLogin = {
                    currentScreen = "Login"
                }
            )

            "Login" -> LoginScreen(
                onLoginSuccess = {
                    currentScreen = "Home"
                },
                onBackToSignUp = {
                    currentScreen = "Main"
                }
            )

            "Home" -> HomePage()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(
                    account.idToken,
                    null
                )

                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { authTask ->
                        if (authTask.isSuccessful) {
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Login Failed: ${authTask.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }

    // ===================== HOME SCREEN =====================
    // ===================== HOME SCREEN =====================
    @Composable
    fun HomePage(userViewModel: UserViewModel = viewModel()) {
        var selectedTab by remember { mutableStateOf(0) }
        var currentSubScreen by remember { mutableStateOf("Main") }
        var registrationData by remember { mutableStateOf(com.example.ict602my_vol.data.RegistrationData()) }
        var showEventDetail by remember { mutableStateOf(false) }
        var selectedEvent by remember { mutableStateOf<Event?>(null) }

        // SATU SCAFFOLD SAHAJA SEBAGAI BASE
        Scaffold(
            bottomBar = {
                // Bottom bar cuma muncul kalau bukan tengah register/success
                if (currentSubScreen == "Main") {
                    BottomNavigationBar(
                        selected = selectedTab,
                        onSelect = { selectedTab = it
                            if (it != 0) showEventDetail = false
                        }
                    )
                }
            }
        ) { paddingValues ->
            // Box ni penting untuk tindihkan skrin Success atas HomeScreen
            Box(modifier = Modifier.fillMaxSize()) {

                // --- LAYER 1: HOMESCREEN (Sentiasa 'hidup' kat belakang) ---
                when (selectedTab) {
                    0 -> HomeScreen(
                        paddingValues = paddingValues,
                        onRegisterClick = { currentSubScreen = "Register" }
                    )
                    1 -> NotificationScreen(paddingValues, userViewModel)
                    2 -> ProfileScreen(
                        padding = paddingValues,
                        userViewModel = userViewModel,
                        onNavigateToActivities = { selectedTab = 3 })
                    3 -> ActivityScreen(
                        padding = paddingValues,
                        onNavigateToProfile = { selectedTab = 2 }
                    )
                }

                // --- LAYER 2: OVERLAY (Hanya muncul bila currentSubScreen bukan "Main") ---
                if (currentSubScreen != "Main") {
                    when (currentSubScreen) {
                        "Register" -> {
                            RegisterScreen(
                                onBack = { currentSubScreen = "Main" },
                                onRegisterSuccess = { data ->
                                    registrationData = data
                                    currentSubScreen = "Success"
                                }
                            )
                        }
                        "Success" -> SuccessScreen(
                            eventName = registrationData?.eventName ?: "Event",
                            onViewRegistration = {
                                currentSubScreen = "View"
                            },
                            onBackToHome = {
                                currentSubScreen = "Main"
                                selectedTab = 0

                                // PENTING: Tutup skrin detail kat sini juga
                                showEventDetail = false
                            }
                        )

                        "View" -> {
                            ViewRegistrationScreen(
                                data = registrationData,
                                onBackToHome = {
                                    // TUTUP OVERLAY
                                    currentSubScreen = "Main"

                                    // RESET TAB KE HOME
                                    selectedTab = 0

                                    // PENTING: Tutup skrin detail supaya nampak Event List balik
                                    showEventDetail = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    // ===================== PREVIEW =====================
    @Preview(showBackground = true)
    @Composable
    fun PreviewApp() {
        EventTest3Theme {
            HomePage()
        }
    }
}