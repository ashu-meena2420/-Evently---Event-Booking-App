package com.example.evently.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.evently.R
import com.example.evently.navigation.Screen
import com.example.evently.viewmodel.EventlyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: EventlyViewModel) {
    var mobileInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Soft glowing background gradient at the top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Professional App Logo
            Image(
                painter = painterResource(id = R.drawable.evently_logo),
                contentDescription = "Evently Logo",
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(24.dp)
                    )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Branding
            Text(
                text = "Welcome to Evently",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter your mobile number to discover events",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Phone Input
            OutlinedTextField(
                value = mobileInput,
                onValueChange = {
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                        mobileInput = it
                        errorMessage = null
                    }
                },
                label = { Text("Mobile Number") },
                placeholder = { Text("Enter 10-digit number") },
                prefix = { Text("+91  ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = "Phone Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                singleLine = true,
                isError = errorMessage != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
                )
            )

            // Error message text
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue Button
            Button(
                onClick = {
                    if (mobileInput.length != 10) {
                        errorMessage = "Please enter a valid 10-digit mobile number."
                    } else {
                        viewModel.setMobileNumber(mobileInput)
                        navController.navigate(Screen.OTP.route)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
                Text(
                    text = "or",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Google Sign-In Button
            OutlinedButton(
                onClick = {
                    // Simulate Login
                    viewModel.login()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = ButtonDefaults.outlinedButtonColors()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Continue with Google",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Terms and Conditions
            Text(
                text = "By continuing, you agree to our Terms of Service and Privacy Policy.",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

