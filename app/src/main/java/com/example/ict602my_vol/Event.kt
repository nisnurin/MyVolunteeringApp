package com.example.ict602my_vol.data

data class VolEvent(
    val id: String = "",
    val name: String = "",
    val organizer: String = "", // Make sure this is the 3rd parameter
    val date: String = "",      // 4th
    val time: String = "",      // 5th (This is what you added)
    val location: String = "",
    val description: String = "",
    val imageUrl: String = ""
)