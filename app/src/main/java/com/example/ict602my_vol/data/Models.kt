package com.example.ict602my_vol.data

// ===================== DATA MODEL =====================
data class Organizer(val title: String, val imageResId: Int)
data class Event(val organizer: String, val name: String, val date: String, val location: String, val imageResId: Int)