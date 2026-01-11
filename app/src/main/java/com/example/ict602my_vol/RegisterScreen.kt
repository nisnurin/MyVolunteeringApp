package com.example.ict602my_vol.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.ict602my_vol.data.RegistrationData
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onBack: () -> Unit, onRegisterSuccess: (RegistrationData) -> Unit) {
    val scrollState = rememberScrollState()

    // States untuk borang lengkap
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

    // Kalendar & Gender States
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
            Text("Register", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(scrollState)) {
            RegisterInput("Full Name", fullName, { fullName = it }, "Ali bin Abu")
            RegisterInput("Nationality", nationality, { nationality = it }, "Malaysia")
            RegisterInput("NRIC", nric, { nric = it }, "050808060145")

            // DOB & Gender
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

            // Postcode & City
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) { RegisterInput("Post Code", postCode, { postCode = it }, "40100") }
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) { RegisterInput("City", city, { city = it }, "Shah Alam") }
            }

            // Residential Country & State
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) { RegisterInput("Residential Country", residentialCountry, { residentialCountry = it }, "Malaysia") }
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) { RegisterInput("State", state, { state = it }, "Selangor") }
            }

            RegisterInput("Blood Type", bloodType, { bloodType = it }, "O-")
            RegisterInput("Emergency Contact Name", emergencyName, { emergencyName = it }, "Fatimah Zahra")
            RegisterInput("Emergency Contact Relationship", emergencyRel, { emergencyRel = it }, "Sister")
            RegisterInput("Emergency Contact Number", emergencyPhone, { emergencyPhone = it }, "017-9657411")

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    onRegisterSuccess(RegistrationData(
                        fullName, nationality, nric, dob, gender, address, postCode, city,
                        residentialCountry, state, bloodType, emergencyName, emergencyRel, emergencyPhone
                    ))
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) { Text("Register", color = Color.White, fontWeight = FontWeight.Bold) }
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
            shape = RoundedCornerShape(8.dp)
        )
    }
}