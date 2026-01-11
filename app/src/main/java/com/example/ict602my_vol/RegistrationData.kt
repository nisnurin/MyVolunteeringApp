package com.example.ict602my_vol.data

data class RegistrationData(
    val fullName: String = "",
    val nationality: String = "",
    val nric: String = "",
    val dob: String = "",
    val gender: String = "",
    val address: String = "",
    val postCode: String = "",
    val city: String = "",
    val state: String = "",
    val residentialCountry: String = "",
    val bloodType: String = "",
    val emergencyContactName: String = "",
    val emergencyContactRelationship: String = "",
    val emergencyContactNumber: String = "",
    val email: String = "Ali123abu@gmail.com", // Default email user
    val eventName: String = "Volunteer Charity Drive 2026",
    val location: String = "Kuala Lumpur",
    val status: String = "Confirmed"
)