package com.example.ict602my_vol

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
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
import com.example.ict602my_vol.data.Event
import com.example.ict602my_vol.data.Organizer
import com.example.ict602my_vol.ui.home.HomeScreen
import com.example.ict602my_vol.ui.BottomNavigationBar
import com.example.ict602my_vol.ui.theme.EventTest3Theme


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
            AppRoot()
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
                onGoogleClick = {
                    signInWithGoogle()
                }
            )
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
    @Composable
    fun MainScreen() {
        var selectedTab by remember { mutableStateOf(0) }

        Scaffold(
            bottomBar = {
                BottomNavigationBar(selectedTab) { selectedTab = it }
            }
        ) { padding ->
            when (selectedTab) {
                0 -> HomeScreen(padding)
                1 -> NotificationScreen(padding)
                2 -> ProfileScreen(padding)
            }
        }
    }


    // ===================== OTHER SCREENS =====================
    @Composable
    fun NotificationScreen(padding: PaddingValues) {
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text("Notification Screen")
        }
    }

    @Composable
    fun ProfileScreen(padding: PaddingValues) {
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text("Profile Screen")
        }
    }


    // ===================== PREVIEW =====================
    @Preview(showBackground = true)
    @Composable
    fun PreviewApp() {
        EventTest3Theme {
            MainScreen()
        }
    }
}