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
                            Toast.makeText(this, "Login Failed: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }
}