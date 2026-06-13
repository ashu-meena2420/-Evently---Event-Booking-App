package com.example.evently.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.evently.model.PaymentCard
import com.example.evently.model.UpiMethod
import com.example.evently.viewmodel.EventlyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(navController: NavController, viewModel: EventlyViewModel) {
    val cards by viewModel.cards.collectAsState()
    val upis by viewModel.upiMethods.collectAsState()

    var showAddCardDialog by remember { mutableStateOf(false) }
    var showAddUpiDialog by remember { mutableStateOf(false) }

    if (showAddCardDialog) {
        AddCardDialog(
            onDismiss = { showAddCardDialog = false },
            onAdd = { num, holder, exp, type ->
                viewModel.addCard(num, holder, exp, type)
                showAddCardDialog = false
            }
        )
    }

    if (showAddUpiDialog) {
        AddUpiDialog(
            onDismiss = { showAddUpiDialog = false },
            onAdd = { upiId, provider ->
                viewModel.addUpiMethod(upiId, provider)
                showAddUpiDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Navigation Header
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
                text = "Payment Methods",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Cards Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Credit & Debit Cards",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    TextButton(onClick = { showAddCardDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Card", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add New", fontWeight = FontWeight.Bold)
                    }
                }

                if (cards.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No credit/debit cards saved.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    cards.forEach { card ->
                        PremiumCardItem(card = card, onDelete = { viewModel.deleteCard(card.id) })
                    }
                }
            }

            // UPI Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "UPI Accounts",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    TextButton(onClick = { showAddUpiDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add UPI", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Link UPI", fontWeight = FontWeight.Bold)
                    }
                }

                if (upis.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No UPI accounts linked.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        upis.forEach { upi ->
                            UpiMethodItem(upi = upi, onDelete = { viewModel.deleteUpiMethod(upi.id) })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PremiumCardItem(card: PaymentCard, onDelete: () -> Unit) {
    // Choose beautiful gradient based on card type
    val cardGradient = when (card.cardType) {
        "Visa" -> Brush.linearGradient(
            colors = listOf(Color(0xFF1E3C72), Color(0xFF2A5298))
        )
        "Mastercard" -> Brush.linearGradient(
            colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
        )
        else -> Brush.linearGradient(
            colors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(cardGradient)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Bank Logo/Type & Delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = card.cardType.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Card",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Card Number
            Text(
                text = card.cardNumber,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )

            // Footer: Holder Name and Expiry
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CARDHOLDER",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = card.cardHolder.uppercase(),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "EXPIRES",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = card.expiryDate,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun UpiMethodItem(upi: UpiMethod, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = upi.provider,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = upi.upiId,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = upi.provider,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Unlink UPI",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun AddCardDialog(onDismiss: () -> Unit, onAdd: (String, String, String, String) -> Unit) {
    var cardNumber by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cardType by remember { mutableStateOf("Visa") }

    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Credit/Debit Card", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (errorMsg != null) {
                    Text(errorMsg!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { if (it.length <= 16 && it.all { c -> c.isDigit() }) cardNumber = it },
                    label = { Text("Card Number (16 digits)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                OutlinedTextField(
                    value = cardHolder,
                    onValueChange = { cardHolder = it },
                    label = { Text("Cardholder Name") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = expiry,
                    onValueChange = {
                        if (it.length <= 5) {
                            expiry = if (it.length == 2 && !it.contains("/") && expiry.length == 1) {
                                "$it/"
                            } else {
                                it
                            }
                        }
                    },
                    label = { Text("Expiry (MM/YY)") },
                    placeholder = { Text("MM/YY") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                // Card Type selection row
                Text("Card Type", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("Visa", "Mastercard").forEach { type ->
                        val isSel = cardType == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.2f))
                                .clickable { cardType = type }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(type, color = if (isSel) Color.White else Color.Black)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (cardNumber.length != 16) {
                        errorMsg = "Card number must be 16 digits."
                    } else if (cardHolder.isBlank()) {
                        errorMsg = "Cardholder name cannot be blank."
                    } else if (expiry.length != 5 || !expiry.contains("/")) {
                        errorMsg = "Expiry must be in MM/YY format."
                    } else {
                        onAdd(cardNumber, cardHolder, expiry, cardType)
                    }
                }
            ) {
                Text("Add Card")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddUpiDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var upiId by remember { mutableStateOf("") }
    var provider by remember { mutableStateOf("GooglePay") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Link UPI Account", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (errorMsg != null) {
                    Text(errorMsg!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("UPI ID (e.g. user@bank)") },
                    singleLine = true
                )

                Text("UPI Provider", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("GooglePay", "PhonePe", "Paytm", "BHIM").forEach { p ->
                        val isSel = provider == p
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.2f))
                                .clickable { provider = p }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(p, color = if (isSel) Color.White else Color.Black)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (upiId.isBlank() || !upiId.contains("@")) {
                        errorMsg = "Please enter a valid UPI ID (e.g., user@okhdfcbank)."
                    } else {
                        onAdd(upiId, provider)
                    }
                }
            ) {
                Text("Link")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
