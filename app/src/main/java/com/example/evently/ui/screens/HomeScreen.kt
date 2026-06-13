package com.example.evently.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: EventlyViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val location by viewModel.selectedLocation.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val wishlistedIds by viewModel.wishlistedIds.collectAsState()

    var showLocationDialog by remember { mutableStateOf(false) }

    val recommendedEvents = remember(selectedCategory, query) {
        viewModel.eventsList.filter { event ->
            (selectedCategory == null || event.category == selectedCategory) &&
                    (query.isEmpty() || event.name.contains(query, ignoreCase = true) || event.venue.contains(query, ignoreCase = true))
        }
    }

    val trendingEvents = remember { viewModel.eventsList.filter { it.rating >= 4.8 }.take(5) }
    val happeningToday = remember { viewModel.eventsList.take(4) }

    if (showLocationDialog) {
        LocationSelectionDialog(
            currentCity = location,
            onCitySelected = { viewModel.setLocation(it); showLocationDialog = false },
            onDismiss = { showLocationDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Top Header ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good evening 👋",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        letterSpacing = 0.3.sp
                    )
                )
                Text(
                    text = user.name.substringBefore(" "),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                val isRefreshing by viewModel.isRefreshing.collectAsState()

                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    IconButton(onClick = { viewModel.refreshEvents() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                // Notification bell with badge
                Box {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.onBackground)
                    }
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(SecondaryLight, CircleShape)
                            .align(Alignment.TopEnd)
                            .offset(x = (-10).dp, y = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))
                AsyncImage(
                    model = user.profileImage,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // ── Scrollable body ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Location row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$location, India",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Change",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable { showLocationDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Search Bar (clickable → navigate) ────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .shadow(6.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { navController.navigate(Screen.Search.route) }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Search events, concerts, comedy...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.Tune, contentDescription = "Filter", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Category Pills with Emojis ────────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(viewModel.categoriesList) { category ->
                    val isSelected = selectedCategory == category
                    val (gradStart, gradEnd) = categoryGradient(category)
                    val emoji = categoryEmojis[category] ?: "✨"

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected)
                                    Brush.horizontalGradient(listOf(gradStart, gradEnd))
                                else
                                    Brush.horizontalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.surface,
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color.Transparent
                                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { viewModel.selectCategory(category) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = emoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ── Featured Carousel ─────────────────────────────────────────────────
            FeaturedBannerCarousel(
                events = trendingEvents.take(3),
                onEventSelected = { event ->
                    viewModel.startBooking(event)
                    navController.navigate(Screen.EventDetails.createRoute(event.id))
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Happening Today Strip ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(SecondaryLight, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Happening Today",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    )
                }
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable { navController.navigate(Screen.Search.route) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(happeningToday) { event ->
                    HappeningTodayCard(
                        event = event,
                        onClick = {
                            viewModel.startBooking(event)
                            navController.navigate(Screen.EventDetails.createRoute(event.id))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Trending Events ───────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trending 🔥",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable { navController.navigate(Screen.Search.route) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(trendingEvents) { event ->
                    val isWishlisted = wishlistedIds.contains(event.id)
                    TrendingEventCard(
                        event = event,
                        isWishlisted = isWishlisted,
                        onWishlistToggle = { viewModel.toggleWishlist(event.id) },
                        onClick = {
                            viewModel.startBooking(event)
                            navController.navigate(Screen.EventDetails.createRoute(event.id))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Recommended For You ───────────────────────────────────────────────
            Text(
                text = "Recommended for You",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (recommendedEvents.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No events match your criteria.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    recommendedEvents.forEach { event ->
                        val isWishlisted = wishlistedIds.contains(event.id)
                        RecommendedEventItem(
                            event = event,
                            isWishlisted = isWishlisted,
                            onWishlistToggle = { viewModel.toggleWishlist(event.id) },
                            onClick = {
                                viewModel.startBooking(event)
                                navController.navigate(Screen.EventDetails.createRoute(event.id))
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Happening Today Card ─────────────────────────────────────────────────────
@Composable
fun HappeningTodayCard(event: Event, onClick: () -> Unit) {
    val (gradStart, gradEnd) = categoryGradient(event.category)
    // Stable pseudo-random viewer count seeded by event id
    val viewerCount = remember(event.id) { (event.id.hashCode() and 0x7FFFFFFF) % 80 + 20 }

    Card(
        modifier = Modifier
            .width(180.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(110.dp)) {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f)))
                )
            )
            // Time badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(Color.Black.copy(0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = event.time, style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
            }
            // 🔥 Viewing now badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(
                        Brush.horizontalGradient(listOf(gradStart, gradEnd)),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(text = "🔥 $viewerCount", style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
            }
        }
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "₹${event.price}+",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

// ── Featured Banner Carousel ──────────────────────────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeaturedBannerCarousel(events: List<Event>, onEventSelected: (Event) -> Unit) {
    if (events.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { events.size })

    LaunchedEffect(pagerState.currentPage) {
        delay(4000)
        pagerState.animateScrollToPage(
            (pagerState.currentPage + 1) % events.size,
            animationSpec = tween(700)
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 14.dp,
            modifier = Modifier.fillMaxWidth().height(220.dp)
        ) { page ->
            val event = events[page]
            val (gradStart, gradEnd) = categoryGradient(event.category)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onEventSelected(event) }
                    .shadow(8.dp, RoundedCornerShape(24.dp))
            ) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = event.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Rich gradient overlay
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                gradEnd.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    // Category gradient chip
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(listOf(gradStart, gradEnd)),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "✦ FEATURED",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color.White),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(event.venue.substringBefore(","), style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.8f))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = LightYellow, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("${event.rating}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dot indicators
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(events.size) { i ->
                val isActive = pagerState.currentPage == i
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(if (isActive) 20.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                        )
                )
            }
        }
    }
}

// ── Trending Event Card ────────────────────────────────────────────────────────
@Composable
fun TrendingEventCard(
    event: Event,
    isWishlisted: Boolean,
    onWishlistToggle: () -> Unit,
    onClick: () -> Unit
) {
    val viewerCount = remember(event.id) { (event.id.hashCode() and 0x7FFFFFFF) % 120 + 15 }
    val (gradStart, gradEnd) = categoryGradient(event.category)

    Card(
        modifier = Modifier
            .width(260.dp)
            .height(270.dp)
            .shadow(6.dp, RoundedCornerShape(22.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().height(155.dp)) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = event.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Favourite button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(34.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        .clickable { onWishlistToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isWishlisted) SecondaryLight else Color.Gray,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // 🔥 Viewer badge bottom-left
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .background(
                            Brush.horizontalGradient(listOf(gradStart.copy(0.9f), gradEnd.copy(0.9f))),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "🔥 $viewerCount viewing", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                }

                // Rating badge bottom-right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = LightYellow, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(event.rating.toString(), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, null, tint = MaterialTheme.colorScheme.onBackground.copy(0.4f), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.date.substringBefore(","),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = "₹${event.price}+",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

// ── Recommended Event Item ────────────────────────────────────────────────────
@Composable
fun RecommendedEventItem(
    event: Event,
    isWishlisted: Boolean,
    onWishlistToggle: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.name,
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
            // Category color dot
            val (gradStart, _) = categoryGradient(event.category)
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(gradStart, CircleShape)
                    .align(Alignment.TopStart)
                    .offset((-3).dp, (-3).dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${categoryEmojis[event.category] ?: ""} ${event.category}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = event.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.onBackground.copy(0.4f), modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = event.venue.substringBefore(","),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, null, tint = MaterialTheme.colorScheme.onBackground.copy(0.4f), modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = event.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.height(90.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        if (isWishlisted) SecondaryLight.copy(0.15f) else MaterialTheme.colorScheme.onBackground.copy(0.05f),
                        CircleShape
                    )
                    .clickable { onWishlistToggle() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isWishlisted) SecondaryLight else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = "₹${event.price}+",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            )
        }
    }
}

// ── Location Dialog ────────────────────────────────────────────────────────────
@Composable
fun LocationSelectionDialog(currentCity: String, onCitySelected: (String) -> Unit, onDismiss: () -> Unit) {
    val cities = listOf("Mumbai", "Bengaluru", "Goa", "Delhi", "Pune", "Kolkata", "Hyderabad")
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Location", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                cities.forEach { city ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (city == currentCity) MaterialTheme.colorScheme.primary.copy(0.08f) else Color.Transparent)
                            .clickable { onCitySelected(city) }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (city == currentCity) FontWeight.Bold else FontWeight.Normal,
                                color = if (city == currentCity) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                            )
                        )
                        if (city == currentCity) {
                            Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
