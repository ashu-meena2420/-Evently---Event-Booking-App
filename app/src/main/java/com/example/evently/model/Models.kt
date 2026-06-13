package com.example.evently.model

data class Event(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val date: String,
    val time: String,
    val venue: String,
    val price: Int, // Starting price
    val rating: Double,
    val category: String,
    val organizer: String,
    val highlights: List<String>,
    val gallery: List<String>
)

data class TicketType(
    val name: String, // "General Admission", "Premium", "VIP"
    val price: Int,
    val features: List<String>
)

data class Booking(
    val id: String,
    val event: Event,
    val ticketType: String,
    val quantity: Int,
    val totalPrice: Int,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val bookingTime: String,
    val qrCodeToken: String,
    val isCompleted: Boolean = false
)

data class User(
    val name: String,
    val email: String,
    val phone: String,
    val profileImage: String
)

data class PaymentCard(
    val id: String,
    val cardNumber: String,
    val cardHolder: String,
    val expiryDate: String,
    val cardType: String // "Visa", "Mastercard"
)

data class UpiMethod(
    val id: String,
    val upiId: String,
    val provider: String // "GooglePay", "PhonePe", "Paytm"
)

