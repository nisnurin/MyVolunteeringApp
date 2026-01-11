package com.example.ict602my_vol

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    // This variable holds the "Source of Truth" for the image
    var selectedImageUri by mutableStateOf<Uri?>(null)
    var userName by mutableStateOf("ALI BIN ABU")
}