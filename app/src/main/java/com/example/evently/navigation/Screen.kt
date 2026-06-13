package com.example.evently.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object OTP : Screen("otp")
    object Home : Screen("home")
    object Search : Screen("search")
    object EventDetails : Screen("event_details/{eventId}") {
        fun createRoute(eventId: String) = "event_details/$eventId"
    }
    object TicketSelection : Screen("ticket_selection/{eventId}") {
        fun createRoute(eventId: String) = "ticket_selection/$eventId"
    }
    object Checkout : Screen("checkout/{eventId}") {
        fun createRoute(eventId: String) = "checkout/$eventId"
    }
    object BookingSuccess : Screen("booking_success/{bookingId}") {
        fun createRoute(bookingId: String) = "booking_success/$bookingId"
    }
    object MyTickets : Screen("my_tickets")
    object Wishlist : Screen("wishlist")
    object Profile : Screen("profile")
    object PaymentMethods : Screen("payment_methods")
    object Settings : Screen("settings")
    object HelpSupport : Screen("help_support")
}

