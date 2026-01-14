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
    val email: String = "", // Biar kosong, nanti kita isi masa register
    val eventName: String = "",
    val location: String = "",
    val status: String = "Pending" // Biasanya status mula-mula adalah Pending
)