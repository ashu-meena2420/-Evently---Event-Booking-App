package com.example.evently.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.evently.viewmodel.EventlyViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(navController: NavController, viewModel: EventlyViewModel) {
    val coroutineScope = rememberCoroutineScope()

    // Form inputs
    var subject by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Booking Help") }
    var description by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val faqItems = listOf(
        FAQItem(
            question = "How do I download my ticket receipt?",
            answer = "Go to 'My Tickets' in the bottom navigation tab, click 'View Ticket' on any upcoming booking, and select 'Download'. The receipt will be exported as a text file to your device's Downloads directory."
        ),
        FAQItem(
            question = "Can I cancel a booking and get a refund?",
            answer = "Tickets once booked cannot be cancelled or refunded under standard guidelines, unless the event itself is postponed or cancelled by the event organizer."
        ),
        FAQItem(
            question = "What payment methods are supported?",
            answer = "We support UPI (GooglePay, PhonePe, Paytm, BHIM), Credit Cards, Debit Cards, and digital wallets. You can manage saved cards in the 'Payment Methods' settings screen."
        ),
        FAQItem(
            question = "Is my credit card information secure?",
            answer = "Yes. All transactions are authorized via secure bank gateways and encrypted merchant APIs. We store only truncated card numbers locally to help you identify your cards."
        )
    )

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Ticket Submitted", fontWeight = FontWeight.Bold) },
            text = { Text("Your support query has been successfully queued. Our customer care desk will contact you via your registered email within 24 hours.") },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    subject = ""
                    description = ""
                }) {
                    Text("OK")
                }
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
                text = "Help & Support",
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
            // FAQs
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Frequently Asked Questions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                faqItems.forEach { item ->
                    FAQCard(item = item)
                }
            }

            // Contact Form
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Submit a Support Ticket",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (errorMsg != null) {
                            Text(errorMsg!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }

                        // Category Selection
                        Text("Category", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Booking Help", "Payments", "Feedback").forEach { cat ->
                                val isSel = category == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.2f))
                                        .clickable { category = cat }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(cat, color = if (isSel) Color.White else Color.Black)
                                }
                            }
                        }

                        // Subject
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Subject Summary") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Description
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Detailed Description") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 5
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (subject.isBlank()) {
                                    errorMsg = "Please fill in the subject summary."
                                } else if (description.isBlank()) {
                                    errorMsg = "Please describe your query in detail."
                                } else {
                                    errorMsg = null
                                    isSubmitting = true
                                    coroutineScope.launch {
                                        kotlinx.coroutines.delay(1500)
                                        isSubmitting = false
                                        showSuccessDialog = true
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            enabled = !isSubmitting
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Mail, contentDescription = "Submit", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Submit Query", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FAQCard(item: FAQItem) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.question,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                    contentDescription = "Expand",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = item.answer,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

data class FAQItem(
    val question: String,
    val answer: String
)
