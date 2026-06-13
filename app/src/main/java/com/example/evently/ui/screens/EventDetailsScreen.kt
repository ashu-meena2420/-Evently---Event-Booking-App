package com.example.evently.ui.screens

import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.evently.model.Event
import com.example.evently.navigation.Screen
import com.example.evently.theme.*
import com.example.evently.viewmodel.EventlyViewModel

@Composable
fun EventDetailsScreen(navController: NavController, eventId: String, viewModel: EventlyViewModel) {
    val context = LocalContext.current
    val event = viewModel.eventsList.find { it.id == eventId }
    val wishlistedIds by viewModel.wishlistedIds.collectAsState()

    if (event == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Event not found.")
        }
        return
    }

    val isWishlisted = wishlistedIds.contains(event.id)
    val (gradStart, gradEnd) = categoryGradient(event.category)

    // Stable pseudo-random seat pressure seeded by event id
    val totalSeats = remember(event.id) { (event.id.hashCode() and 0x7FFFFFFF) % 200 + 100 }
    val seatsBooked = remember(event.id) { (event.id.hashCode() and 0x7FFFFFFF) % (totalSeats - 10) + 10 }
    val seatsLeft = totalSeats - seatsBooked
    val fillFraction = seatsBooked.toFloat() / totalSeats.toFloat()
    val viewerCount = remember(event.id) { (event.id.hashCode() and 0x7FFFFFFF) % 120 + 15 }

    // Animated progress for seat bar
    val animatedFill by animateFloatAsState(
        targetValue = fillFraction,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "seatFill"
    )

    val pressureColor = when {
        fillFraction > 0.85f -> Color(0xFFEF4444)
        fillFraction > 0.65f -> OrangeWarn
        else -> SuccessColor
    }
    val pressureLabel = when {
        fillFraction > 0.85f -> "Almost Full!"
        fillFraction > 0.65f -> "Filling Fast"
        else -> "Available"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {
            // ── Hero Image Banner ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = event.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay with category color
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(0.4f),
                                Color.Transparent,
                                gradEnd.copy(0.2f),
                                Color.Black.copy(0.75f)
                            )
                        )
                    )
                )

                // Back + action row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(40.dp).background(Color.Black.copy(0.4f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        IconButton(
                            onClick = { viewModel.toggleWishlist(event.id) },
                            modifier = Modifier.size(40.dp).background(Color.Black.copy(0.4f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isWishlisted) SecondaryLight else Color.White
                            )
                        }
                        IconButton(
                            onClick = {
                                val shareText = "Check out ${event.name}!\n${event.date} • ${event.time}\n${event.venue}"
                                context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }, null))
                            },
                            modifier = Modifier.size(40.dp).background(Color.Black.copy(0.4f), CircleShape)
                        ) {
                            Icon(Icons.Default.Share, null, tint = Color.White)
                        }
                    }
                }

                // Category chip bottom-left of image
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(18.dp)
                        .background(
                            Brush.horizontalGradient(listOf(gradStart, gradEnd)),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${categoryEmojis[event.category] ?: ""} ${event.category.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                }

                // Viewer count badge bottom-right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(18.dp)
                        .background(Color.Black.copy(0.65f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🔥 $viewerCount viewing now",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            // ── Info Block ───────────────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            ) {
                // Title
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = LightYellow, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${event.rating} (500+ ratings)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "• By ${event.organizer}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground.copy(0.55f))
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 18.dp), color = MaterialTheme.colorScheme.onBackground.copy(0.08f))

                // ── Live Seat Pressure Card ──────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = pressureColor.copy(alpha = 0.07f)),
                    border = BorderStroke(1.dp, pressureColor.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(pressureColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = pressureLabel,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = pressureColor
                                    )
                                )
                            }
                            Text(
                                text = "$seatsLeft seats left",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Animated progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedFill)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(pressureColor.copy(0.7f), pressureColor)
                                        )
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "$seatsBooked of $totalSeats seats booked",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 18.dp), color = MaterialTheme.colorScheme.onBackground.copy(0.08f))

                // Date & Venue
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier.size(46.dp).background(
                            Brush.linearGradient(listOf(gradStart.copy(0.15f), gradEnd.copy(0.15f))),
                            RoundedCornerShape(13.dp)
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(event.date, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(event.time, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(0.6f))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val localContext = LocalContext.current
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                        .clickable {
                            val mapUri = android.net.Uri.parse("geo:0,0?q=" + android.net.Uri.encode(event.venue))
                            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri).apply { setPackage("com.google.android.apps.maps") }
                            try { localContext.startActivity(mapIntent) } catch (e: Exception) {
                                val webUri = android.net.Uri.parse("https://www.google.com/maps/search/?api=1&query=" + android.net.Uri.encode(event.venue))
                                localContext.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                            }
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.size(46.dp).background(
                            Brush.linearGradient(listOf(gradStart.copy(0.15f), gradEnd.copy(0.15f))),
                            RoundedCornerShape(13.dp)
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PinDrop, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(event.venue.substringBefore(","), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(event.venue.substringAfter(", "), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Get Directions", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Navigation, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(11.dp))
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 18.dp), color = MaterialTheme.colorScheme.onBackground.copy(0.08f))

                // About
                Text("About the Event", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp, color = MaterialTheme.colorScheme.onBackground.copy(0.7f))
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Highlights
                Text("Event Highlights", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    event.highlights.chunked(2).forEach { rowItems ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowItems.forEach { highlight ->
                                HighlightChip(text = highlight, modifier = Modifier.weight(1f))
                            }
                            if (rowItems.size < 2) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Gallery
                Text("Event Gallery", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(event.gallery) { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier.width(150.dp).height(100.dp).clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        // ── Sticky Bottom Bar ────────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
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
                    Text("Starting from", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                    Text(
                        text = "₹${event.price}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    )
                }

                Button(
                    onClick = { navController.navigate(Screen.TicketSelection.createRoute(event.id)) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .height(52.dp)
                            .padding(horizontal = 24.dp)
                            .background(
                                Brush.horizontalGradient(listOf(gradStart, gradEnd)),
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Book Now →", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold), color = Color.White, modifier = Modifier.padding(horizontal = 24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HighlightChip(text: String, modifier: Modifier = Modifier) {
    val icon: ImageVector = when (text) {
        "Free Parking"          -> Icons.Default.LocalParking
        "Food Available"        -> Icons.Default.Fastfood
        "Food & Alcohol"        -> Icons.Default.LocalBar
        "Family Friendly"       -> Icons.Default.FamilyRestroom
        "Wheelchair Accessible" -> Icons.AutoMirrored.Filled.Accessible
        else                    -> Icons.Default.Done
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(0.06f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground.copy(0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
