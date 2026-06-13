package com.example.evently.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.evently.navigation.Screen
import com.example.evently.viewmodel.EventlyViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: EventlyViewModel,
    onTabSelect: (Int) -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showPlaceholderDialog by remember { mutableStateOf<String?>(null) }

    if (showEditDialog) {
        EditProfileDialog(
            name = user.name,
            email = user.email,
            phone = user.phone,
            onSave = { name, email, phone ->
                viewModel.updateProfile(name, email, phone)
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }

    if (showPlaceholderDialog != null) {
        AlertDialog(
            onDismissRequest = { showPlaceholderDialog = null },
            title = { Text(showPlaceholderDialog!!, fontWeight = FontWeight.Bold) },
            text = { Text("This is a premium simulated view. Full settings functionality will be integrated in future release phases.") },
            confirmButton = {
                TextButton(onClick = { showPlaceholderDialog = null }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Banner / Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                .statusBarsPadding()
                .padding(top = 32.dp, bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Profile image
                AsyncImage(
                    model = user.profileImage,
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menu Sections
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileMenuItem(
                icon = Icons.Default.Edit,
                title = "Edit Profile",
                subtitle = "Update your name, email, and phone"
            ) {
                showEditDialog = true
            }

            ProfileMenuItem(
                icon = Icons.Default.ConfirmationNumber,
                title = "My Tickets",
                subtitle = "View your active & completed bookings"
            ) {
                // Select Tickets tab (tab index 2)
                onTabSelect(2)
            }

            ProfileMenuItem(
                icon = Icons.Default.Favorite,
                title = "Saved Events",
                subtitle = "Browse your favorited events checklist"
            ) {
                // Select Wishlist tab (tab index 3)
                onTabSelect(3)
            }

            ProfileMenuItem(
                icon = Icons.Default.Payment,
                title = "Payment Methods",
                subtitle = "Manage UPI, debit cards, and wallets"
            ) {
                navController.navigate(Screen.PaymentMethods.route)
            }

            ProfileMenuItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                subtitle = "Dark mode, alerts, and system configuration"
            ) {
                navController.navigate(Screen.Settings.route)
            }

            ProfileMenuItem(
                icon = Icons.Default.HelpOutline,
                title = "Help & Support",
                subtitle = "Frequently asked questions & customer care support"
            ) {
                navController.navigate(Screen.HelpSupport.route)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout MenuItem Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier
                        .clickable {
                            viewModel.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun EditProfileDialog(
    name: String,
    email: String,
    phone: String,
    onSave: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var tempName by remember { mutableStateOf(name) }
    var tempEmail by remember { mutableStateOf(email) }
    var tempPhone by remember { mutableStateOf(phone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Name") }
                )
                OutlinedTextField(
                    value = tempEmail,
                    onValueChange = { tempEmail = it },
                    label = { Text("Email") }
                )
                OutlinedTextField(
                    value = tempPhone,
                    onValueChange = { tempPhone = it },
                    label = { Text("Phone") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(tempName, tempEmail, tempPhone) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
