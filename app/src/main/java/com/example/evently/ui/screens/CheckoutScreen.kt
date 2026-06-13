package com.example.evently.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.evently.navigation.Screen
import com.example.evently.viewmodel.EventlyViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController, eventId: String, viewModel: EventlyViewModel) {
    val event = viewModel.eventsList.find { it.id == eventId }
    val selectedType by viewModel.activeBookingType.collectAsState()
    val quantity by viewModel.activeBookingQuantity.collectAsState()

    val customerName by viewModel.checkoutName.collectAsState()
    val customerPhone by viewModel.checkoutPhone.collectAsState()
    val customerEmail by viewModel.checkoutEmail.collectAsState()

    var selectedPaymentMethod by remember { mutableStateOf("UPI") }

    var paymentState by remember { mutableStateOf("IDLE") }
    var validationError by remember { mutableStateOf<String?>(null) }
    var pinInput by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    if (event == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Event not found.")
        }
        return
    }

    val (subtotal, platformFee, gst) = viewModel.getBreakdown()
    val total = viewModel.calculateTotalBookingAmount()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Checkout Summary",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section 1: Event Recap
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = event.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${event.date} • ${event.time}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            text = event.venue,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row {
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$selectedType x $quantity",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }

                // Section 2: Customer Details
                Text(
                    text = "Customer Details",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { viewModel.checkoutName.value = it },
                    label = { Text("Name") },
                    leadingIcon = { Icon(Icons.Default.Person, "Name", tint = MaterialTheme.colorScheme.primary) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
                    )
                )

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { viewModel.checkoutPhone.value = it },
                    label = { Text("Mobile Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, "Phone", tint = MaterialTheme.colorScheme.primary) },
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
                    )
                )

                OutlinedTextField(
                    value = customerEmail,
                    onValueChange = { viewModel.checkoutEmail.value = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Mail, "Email", tint = MaterialTheme.colorScheme.primary) },
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
                    )
                )

                // Section 3: Payment Summary Bill details
                Text(
                    text = "Payment Summary",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BillRow(label = "Ticket Price ($selectedType x $quantity)", amount = "₹$subtotal")
                        BillRow(label = "Platform Fee", amount = "₹$platformFee")
                        BillRow(label = "GST (18%)", amount = "₹$gst")
                        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Amount",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "₹$total",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }

                // Section 4: Payment Methods
                Text(
                    text = "Select Payment Method",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val paymentMethods = listOf(
                        PaymentMethodItem("UPI", "Pay using GooglePay, PhonePe, BHIM", Icons.Default.QrCode),
                        PaymentMethodItem("Credit Card", "Pay securely with Visa, Mastercard", Icons.Default.CreditCard),
                        PaymentMethodItem("Debit Card", "Pay directly from bank account", Icons.Default.CreditCard),
                        PaymentMethodItem("Wallet", "Pay using PayTM, AmazonPay", Icons.Default.Wallet)
                    )

                    paymentMethods.forEach { method ->
                        val isSelected = selectedPaymentMethod == method.name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedPaymentMethod = method.name }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = method.icon,
                                    contentDescription = method.name,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = method.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = method.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedPaymentMethod = method.name },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
        }

        // Floating Pay Now Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .shadow(16.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Final Total",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "₹$total",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Button(
                    onClick = {
                        if (customerName.isBlank() || customerPhone.isBlank() || customerEmail.isBlank()) {
                            validationError = "Please fill in all customer details to proceed."
                        } else if (customerPhone.length < 10) {
                            validationError = "Please enter a valid 10-digit phone number."
                        } else if (!customerEmail.contains("@")) {
                            validationError = "Please enter a valid email address."
                        } else {
                            paymentState = "SECURING"
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(1500)
                                paymentState = "PIN_ENTRY"
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .width(180.dp)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Pay Now",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }

        // Secure payment simulation dialogs
        if (paymentState == "SECURING") {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Securing Connection...", fontWeight = FontWeight.Bold) },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Text("Contacting secure merchant servers...")
                    }
                },
                confirmButton = {}
            )
        }

        if (paymentState == "PIN_ENTRY") {
            AlertDialog(
                onDismissRequest = { paymentState = "IDLE"; pinInput = "" },
                title = { Text("Enter 4-Digit UPI PIN", fontWeight = FontWeight.Bold) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Authorizing payment of ₹$total for ${event.name}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) pinInput = it },
                            placeholder = { Text("••••") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            modifier = Modifier.width(120.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (pinInput.length == 4) {
                                paymentState = "AUTHORIZING"
                                coroutineScope.launch {
                                    kotlinx.coroutines.delay(1800)
                                    viewModel.confirmBooking()
                                    val lastBooking = viewModel.lastCreatedBooking.value
                                    if (lastBooking != null) {
                                        paymentState = "IDLE"
                                        pinInput = ""
                                        navController.navigate(Screen.BookingSuccess.createRoute(lastBooking.id)) {
                                            popUpTo(Screen.Home.route) { inclusive = false }
                                        }
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { paymentState = "IDLE"; pinInput = "" }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (paymentState == "AUTHORIZING") {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Authorizing Payment...", fontWeight = FontWeight.Bold) },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Text("Processing transaction with bank. Do not close the app.")
                    }
                },
                confirmButton = {}
            )
        }

        if (validationError != null) {
            AlertDialog(
                onDismissRequest = { validationError = null },
                title = { Text("Input Error", fontWeight = FontWeight.Bold) },
                text = { Text(validationError!!) },
                confirmButton = {
                    Button(onClick = { validationError = null }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun BillRow(label: String, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

data class PaymentMethodItem(
    val name: String,
    val description: String,
    val icon: ImageVector
)
