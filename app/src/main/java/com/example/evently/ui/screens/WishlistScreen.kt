package com.example.evently.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.evently.navigation.Screen
import com.example.evently.viewmodel.EventlyViewModel

@Composable
fun WishlistScreen(navController: NavController, viewModel: EventlyViewModel) {
    val wishlistedIds by viewModel.wishlistedIds.collectAsState()

    val wishlistedEvents = remember(wishlistedIds) {
        viewModel.eventsList.filter { wishlistedIds.contains(it.id) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Text(
            text = "Saved Events",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp)
        ) {
            if (wishlistedEvents.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "No Saved Events",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "You haven't saved any events yet.\nBrowse events and tap the heart icon to save them.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(wishlistedEvents) { event ->
                        RecommendedEventItem(
                            event = event,
                            isWishlisted = true,
                            onWishlistToggle = { viewModel.toggleWishlist(event.id) },
                            onClick = {
                                viewModel.startBooking(event)
                                navController.navigate(Screen.EventDetails.createRoute(event.id))
                            }
                        )
                    }
                }
            }
        }
    }
}
