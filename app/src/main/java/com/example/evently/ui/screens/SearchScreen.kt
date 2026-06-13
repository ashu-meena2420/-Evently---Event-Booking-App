package com.example.evently.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.evently.model.Event
import com.example.evently.navigation.Screen
import com.example.evently.theme.LightYellow
import com.example.evently.theme.SecondaryLight
import com.example.evently.theme.categoryEmojis
import com.example.evently.theme.categoryGradient
import com.example.evently.viewmodel.EventlyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: EventlyViewModel) {
    val query by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val maxPrice by viewModel.selectedPriceRangeMax.collectAsState()
    val location by viewModel.selectedLocation.collectAsState()
    val wishlistedIds by viewModel.wishlistedIds.collectAsState()

    var activeSearchQuery by remember { mutableStateOf(query) }
    var selectedFilterCategory by remember { mutableStateOf(selectedCategory) }
    var selectedFilterPrice by remember { mutableStateOf(maxPrice) }
    var selectedFilterLocation by remember { mutableStateOf(location) }

    var showFilterDialog by remember { mutableStateOf(false) }

    val recentSearches = listOf("Arijit Singh", "Comedy Night", "EDM Goa")
    val trendingSearches = listOf("IPL Fan Fest", "Food Carnival", "Networking", "Theatre")

    // Filter events locally based on active filters
    val filteredEvents = remember(activeSearchQuery, selectedFilterCategory, selectedFilterPrice, selectedFilterLocation) {
        viewModel.eventsList.filter { event ->
            val matchesQuery = activeSearchQuery.isEmpty() ||
                    event.name.contains(activeSearchQuery, ignoreCase = true) ||
                    event.venue.contains(activeSearchQuery, ignoreCase = true) ||
                    event.description.contains(activeSearchQuery, ignoreCase = true)

            val matchesCategory = selectedFilterCategory == null || event.category == selectedFilterCategory
            val matchesPrice = event.price <= selectedFilterPrice
            val matchesLocation = event.venue.contains(selectedFilterLocation, ignoreCase = true)

            matchesQuery && matchesCategory && matchesPrice && matchesLocation
        }
    }

    if (showFilterDialog) {
        FilterBottomSheetDialog(
            categories = viewModel.categoriesList,
            selectedCategory = selectedFilterCategory,
            selectedPrice = selectedFilterPrice,
            selectedLocation = selectedFilterLocation,
            onApply = { cat, price, loc ->
                selectedFilterCategory = cat
                selectedFilterPrice = price
                selectedFilterLocation = loc
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        // Search Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            // Search Bar Input
            OutlinedTextField(
                value = activeSearchQuery,
                onValueChange = { activeSearchQuery = it },
                placeholder = { Text("Search by name, location...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                },
                trailingIcon = {
                    if (activeSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { activeSearchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { viewModel.updateSearchQuery(activeSearchQuery) }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            // Filter Trigger Button
            IconButton(
                onClick = { showFilterDialog = true },
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filters",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Selected Active Filters Row
        if (selectedFilterCategory != null || selectedFilterPrice < 2000 || selectedFilterLocation != "Mumbai") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedFilterCategory?.let {
                    FilterChipItem(text = it) { selectedFilterCategory = null }
                }
                if (selectedFilterPrice < 2000) {
                    FilterChipItem(text = "Under ₹$selectedFilterPrice") { selectedFilterPrice = 2000 }
                }
                if (selectedFilterLocation != "Mumbai") {
                    FilterChipItem(text = selectedFilterLocation) { selectedFilterLocation = "Mumbai" }
                }
            }
        }

        // Content
        if (activeSearchQuery.isEmpty() && selectedFilterCategory == null && selectedFilterPrice == 2000 && selectedFilterLocation == "Mumbai") {
            // Display empty search dashboard with Recent and Trending
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Recent Searches
                Text(
                    text = "Recent Searches",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                recentSearches.forEach { search ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                activeSearchQuery = search
                                viewModel.updateSearchQuery(search)
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = search,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Trending Searches Tags Flow
                Text(
                    text = "Trending Searches",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Quick tag row
                    trendingSearches.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    activeSearchQuery = tag
                                    viewModel.updateSearchQuery(tag)
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        } else {
            // Display Results Grid
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {
                if (filteredEvents.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "No results",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No results found for \"$activeSearchQuery\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredEvents) { event ->
                            val isWishlisted = wishlistedIds.contains(event.id)
                            SearchGridCard(
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
            }
        }
    }
}

@Composable
fun FilterChipItem(text: String, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(14.dp)
                    .clickable { onRemove() }
            )
        }
    }
}

@Composable
fun SearchGridCard(
    event: Event,
    isWishlisted: Boolean,
    onWishlistToggle: () -> Unit,
    onClick: () -> Unit
) {
    val (gradStart, gradEnd) = categoryGradient(event.category)
    val viewerCount = remember(event.id) { (event.id.hashCode() and 0x7FFFFFFF) % 80 + 10 }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = event.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(0.5f))
                            )
                        )
                )

                // Wishlist button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(30.dp)
                        .background(Color.White.copy(alpha = 0.9f), shape = CircleShape)
                        .clickable { onWishlistToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isWishlisted) SecondaryLight else Color.Gray,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // 🔥 Viewer badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(gradStart.copy(0.85f), gradEnd.copy(0.85f))
                            ),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "🔥 $viewerCount",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${categoryEmojis[event.category] ?: ""} ${event.date.substringBefore(",")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "₹${event.price}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun FilterBottomSheetDialog(
    categories: List<String>,
    selectedCategory: String?,
    selectedPrice: Int,
    selectedLocation: String,
    onApply: (String?, Int, String) -> Unit,
    onDismiss: () -> Unit
) {
    var tempCategory by remember { mutableStateOf(selectedCategory) }
    var tempPrice by remember { mutableStateOf(selectedPrice.toFloat()) }
    var tempLocation by remember { mutableStateOf(selectedLocation) }

    val locations = listOf("Mumbai", "Bengaluru", "Goa", "Delhi")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filters", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Text(
                    text = "Reset All",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable {
                        tempCategory = null
                        tempPrice = 2000f
                        tempLocation = "Mumbai"
                    }
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category Filter
                Text("Category", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSel = tempCategory == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.1f))
                                .clickable { tempCategory = if (isSel) null else category }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = category,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                // Price Filter
                Text(
                    text = "Max Price: ₹${tempPrice.toInt()}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Slider(
                    value = tempPrice,
                    onValueChange = { tempPrice = it },
                    valueRange = 199f..2000f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Location Filter
                Text("Location", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    locations.forEach { loc ->
                        val isSel = tempLocation == loc
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.1f))
                                .clickable { tempLocation = loc }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = loc,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onApply(tempCategory, tempPrice.toInt(), tempLocation) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Apply Filters")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
