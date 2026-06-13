package com.example.evently.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.evently.model.Booking
import com.example.evently.navigation.Screen
import com.example.evently.theme.*
import com.example.evently.viewmodel.EventlyViewModel

@Composable
fun MyTicketsScreen(navController: NavController, viewModel: EventlyViewModel) {
    val bookings by viewModel.bookings.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    val filteredBookings = remember(bookings, selectedTab) {
        if (selectedTab == 0) bookings.filter { !it.isCompleted }
        else bookings.filter { it.isCompleted }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "My Tickets",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${bookings.size} total booking${if (bookings.size != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
            )
        }

        // ── Tabs ──────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            listOf("Upcoming", "Completed").forEachIndexed { index, label ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                        .clickable { selectedTab = index }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground.copy(0.5f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── List ──────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            if (filteredBookings.isEmpty()) {
                TicketsEmptyState(selectedTab = selectedTab, onExplore = { navController.navigate(Screen.Home.route) })
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredBookings) { booking ->
                        PremiumTicketListCard(
                            booking = booking,
                            onViewTicket = { navController.navigate(Screen.BookingSuccess.createRoute(booking.id)) }
                        )
                    }
                }
            }
        }
    }
}

// ── Premium Ticket List Card ──────────────────────────────────────────────────
@Composable
fun PremiumTicketListCard(booking: Booking, onViewTicket: () -> Unit) {
    val (gradStart, gradEnd) = categoryGradient(booking.event.category)

    // Shimmer animation
    val shimmerInfinite = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by shimmerInfinite.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(2800, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmerX"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                shadowElevation = 12.dp.toPx()
                shape = RoundedCornerShape(20.dp)
                clip = true
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
        ) {
            // ── Gradient header ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(gradStart, gradEnd)))
            ) {
                // Shimmer sweep
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color.Transparent, Color.White.copy(0.1f), Color.Transparent),
                                start = Offset(shimmerOffset * 600f - 200f, 0f),
                                end = Offset(shimmerOffset * 600f + 200f, 200f)
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Event image circle
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(0.15f))
                    ) {
                        AsyncImage(
                            model = booking.event.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = booking.event.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color.White),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, null, tint = Color.White.copy(0.7f), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(booking.event.date, style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(0.8f)))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = Color.White.copy(0.7f), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                booking.event.venue.substringBefore(","),
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(0.8f)),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Status badge
                    Box(
                        modifier = Modifier
                            .background(
                                if (booking.isCompleted) Color.White.copy(0.2f) else Color.White.copy(0.25f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (booking.isCompleted) "DONE" else "✓ LIVE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, color = Color.White)
                        )
                    }
                }
            }

            // ── Perforated separator ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Box(
                    modifier = Modifier.size(20.dp).offset(x = (-10).dp).align(Alignment.CenterStart)
                        .background(MaterialTheme.colorScheme.background, CircleShape)
                )
                Box(
                    modifier = Modifier.size(20.dp).offset(x = 10.dp).align(Alignment.CenterEnd)
                        .background(MaterialTheme.colorScheme.background, CircleShape)
                )
                Canvas(modifier = Modifier.fillMaxWidth().height(1.dp).padding(horizontal = 16.dp).align(Alignment.Center)) {
                    var x = 0f
                    while (x < size.width) {
                        drawLine(Color.Gray.copy(0.25f), Offset(x, 0f), Offset((x + 10f).coerceAtMost(size.width), 0f), 2.5f)
                        x += 18f
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // ── Bottom info bar ───────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${categoryEmojis[booking.event.category] ?: "🎟"} ${booking.ticketType} × ${booking.quantity}",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = "₹${booking.totalPrice} paid",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                    )
                }

                Button(
                    onClick = onViewTicket,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(listOf(gradStart, gradEnd)),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QrCode, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("View Ticket", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color.White))
                        }
                    }
                }
            }
        }
    }
}

// ── Empty State ────────────────────────────────────────────────────────────────
@Composable
fun TicketsEmptyState(selectedTab: Int, onExplore: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated ticket icon
        val pulse = rememberInfiniteTransition(label = "pulse")
        val scale by pulse.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "scale"
        )

        Box(
            modifier = Modifier
                .size(110.dp)
                .background(MaterialTheme.colorScheme.primary.copy(0.08f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ConfirmationNumber,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(0.3f),
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (selectedTab == 0) "No upcoming events" else "No past events",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (selectedTab == 0) "Book tickets to see them here" else "Your attended events will appear here",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground.copy(0.5f)),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (selectedTab == 0) {
            Button(
                onClick = onExplore,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Explore, null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Explore Events", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
            }
        }
    }
}
