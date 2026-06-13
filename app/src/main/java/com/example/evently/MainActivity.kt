package com.example.evently

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.evently.navigation.Screen
import com.example.evently.theme.EventlyTheme
import com.example.evently.ui.screens.*
import com.example.evently.viewmodel.EventlyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: EventlyViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val useDarkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            EventlyTheme(darkTheme = useDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppContent(viewModel: EventlyViewModel) {
    val navController = rememberNavController()
    val hasCompletedOnboarding by viewModel.hasCompletedOnboarding.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if bottom bar should be visible
    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.MyTickets.route,
        Screen.Wishlist.route,
        Screen.Profile.route
    )
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val items = listOf(
                        BottomNavItem("Home", Screen.Home.route, Icons.Default.Home),
                        BottomNavItem("Search", Screen.Search.route, Icons.Default.Search),
                        BottomNavItem("Tickets", Screen.MyTickets.route, Icons.Default.ConfirmationNumber),
                        BottomNavItem("Wishlist", Screen.Wishlist.route, Icons.Default.Favorite),
                        BottomNavItem("Profile", Screen.Profile.route, Icons.Default.Person)
                    )

                    items.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    navController = navController,
                    hasCompletedOnboarding = hasCompletedOnboarding,
                    isLoggedIn = isLoggedIn
                )
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(navController = navController, viewModel = viewModel)
            }

            composable(Screen.Login.route) {
                LoginScreen(navController = navController, viewModel = viewModel)
            }

            composable(Screen.OTP.route) {
                OTPScreen(navController = navController, viewModel = viewModel)
            }

            composable(Screen.Home.route) {
                HomeScreen(navController = navController, viewModel = viewModel)
            }

            composable(Screen.Search.route) {
                SearchScreen(navController = navController, viewModel = viewModel)
            }

            composable(
                route = Screen.EventDetails.route,
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId").orEmpty()
                EventDetailsScreen(
                    navController = navController,
                    eventId = eventId,
                    viewModel = viewModel
                )
            }

            composable(
                route = Screen.TicketSelection.route,
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId").orEmpty()
                TicketSelectionScreen(
                    navController = navController,
                    eventId = eventId,
                    viewModel = viewModel
                )
            }

            composable(
                route = Screen.Checkout.route,
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId").orEmpty()
                CheckoutScreen(
                    navController = navController,
                    eventId = eventId,
                    viewModel = viewModel
                )
            }

            composable(
                route = Screen.BookingSuccess.route,
                arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getString("bookingId").orEmpty()
                BookingSuccessScreen(
                    navController = navController,
                    bookingId = bookingId,
                    viewModel = viewModel
                )
            }

            composable(Screen.MyTickets.route) {
                MyTicketsScreen(navController = navController, viewModel = viewModel)
            }

            composable(Screen.Wishlist.route) {
                WishlistScreen(navController = navController, viewModel = viewModel)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    viewModel = viewModel,
                    onTabSelect = { tabIndex ->
                        val route = when (tabIndex) {
                            0 -> Screen.Home.route
                            1 -> Screen.Search.route
                            2 -> Screen.MyTickets.route
                            3 -> Screen.Wishlist.route
                            else -> Screen.Profile.route
                        }
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Screen.PaymentMethods.route) {
                PaymentMethodsScreen(navController = navController, viewModel = viewModel)
            }

            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController, viewModel = viewModel)
            }

            composable(Screen.HelpSupport.route) {
                HelpSupportScreen(navController = navController, viewModel = viewModel)
            }
        }
    }
}

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
