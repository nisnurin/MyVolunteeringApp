package com.example.ict602my_vol.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.*
import com.example.ict602my_vol.data.RegistrationData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onBack: () -> Unit, onRegisterSuccess: (RegistrationData) -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // --- STATES ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var nric by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var postCode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var residentialCountry by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("") }
    var emergencyName by remember { mutableStateOf("") }
    var emergencyRel by remember { mutableStateOf("") }
    var emergencyPhone by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var genderExpanded by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        dob = formatter.format(Date(it))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Header
        Row(modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            Text("Register Volunteer", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(scrollState)) {

            Text("Login Information", fontWeight = FontWeight.Bold, color = Color.Gray)
            RegisterInput("Email", email, { email = it }, "example@gmail.com")

            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                Text(text = "Password", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text(text = "Min 6 characters", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Personal Information", fontWeight = FontWeight.Bold, color = Color.Gray)

            RegisterInput("Full Name", fullName, { fullName = it }, "Ali bin Abu")
            RegisterInput("Nationality", nationality, { nationality = it }, "Malaysia")
            RegisterInput("NRIC", nric, { nric = it }, "050808060145")

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Date of Birth", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = dob, onValueChange = {}, readOnly = true,
                        trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.DateRange, null) } },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Gender", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    ExposedDropdownMenuBox(expanded = genderExpanded, onExpandedChange = { genderExpanded = !genderExpanded }) {
                        OutlinedTextField(
                            value = gender, onValueChange = {}, readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(expanded = genderExpanded, onDismissRequest = { genderExpanded = false }) {
                            listOf("Male", "Female").forEach { option ->
                                DropdownMenuItem(text = { Text(option) }, onClick = { gender = option; genderExpanded = false })
                            }
                        }
                    }
                }
            }

            RegisterInput("Address", address, { address = it }, "No. 12, Jalan Kenanga 3")

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) { RegisterInput("Post Code", postCode, { postCode = it }, "40100") }
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) { RegisterInput("City", city, { city = it }, "Shah Alam") }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) { RegisterInput("Residential Country", residentialCountry, { residentialCountry = it }, "Malaysia") }
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) { RegisterInput("State", state, { state = it }, "Selangor") }
            }

            RegisterInput("Blood Type", bloodType, { bloodType = it }, "O-")

            Spacer(modifier = Modifier.height(16.dp))
            Text("Emergency Contact", fontWeight = FontWeight.Bold, color = Color.Gray)

            RegisterInput("Contact Name", emergencyName, { emergencyName = it }, "Fatimah Zahra")
            RegisterInput("Relationship", emergencyRel, { emergencyRel = it }, "Sister")
            RegisterInput("Phone Number", emergencyPhone, { emergencyPhone = it }, "017-9657411")

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                        Toast.makeText(context, "Email, Password & Name are required!", Toast.LENGTH_SHORT).show()
                    } else if (password.length < 6) {
                        Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    } else {
                        isLoading = true
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userId = auth.currentUser?.uid
                                    val userProfile = hashMapOf(
                                        "email" to email,
                                        "fullName" to fullName,
                                        "nric" to nric,
                                        "dob" to dob,
                                        "gender" to gender,
                                        "bloodType" to bloodType,
                                        "address" to address,
                                        "city" to city,
                                        "state" to state,
                                        "emergencyContactName" to emergencyName,
                                        "emergencyContactPhone" to emergencyPhone,
                                        "role" to "volunteer"
                                    )

                                    if (userId != null) {
                                        db.collection("events").document(userId)
                                            .set(userProfile)
                                            .addOnSuccessListener {
                                                isLoading = false
                                                Toast.makeText(context, "Registration Success!", Toast.LENGTH_SHORT).show()
                                                onRegisterSuccess(RegistrationData(
                                                    fullName = fullName,
                                                    nationality = nationality,
                                                    nric = nric,
                                                    dob = dob,
                                                    gender = gender,
                                                    address = address,
                                                    postCode = postCode,
                                                    city = city,
                                                    state = state,
                                                    residentialCountry = residentialCountry,
                                                    bloodType = bloodType,
                                                    emergencyContactName = emergencyName,
                                                    emergencyContactRelationship = emergencyRel,
                                                    emergencyContactNumber = emergencyPhone,
                                                    email = email,
                                                    status = "Pending"
                                                ))
                                            }
                                            .addOnFailureListener {
                                                isLoading = false
                                                Toast.makeText(context, "Firestore Error: ${it.message}", Toast.LENGTH_LONG).show()
                                            }
                                    }
                                } else {
                                    isLoading = false
                                    Toast.makeText(context, "Auth Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Register", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun RegisterInput(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }
}