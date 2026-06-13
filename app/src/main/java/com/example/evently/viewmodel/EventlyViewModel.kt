package com.example.evently.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evently.data.DummyData
import com.example.evently.data.local.EventlyDatabase
import com.example.evently.data.repository.DefaultEventlyRepository
import com.example.evently.data.repository.EventlyRepository
import com.example.evently.model.Booking
import com.example.evently.model.Event
import com.example.evently.model.User
import com.example.evently.model.PaymentCard
import com.example.evently.model.UpiMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventlyViewModel(application: Application) : AndroidViewModel(application) {

    // Database & Repository references
    private val database = EventlyDatabase(application)
    private val repository: EventlyRepository = DefaultEventlyRepository(database)

    // Onboarding and Auth State
    val hasCompletedOnboarding: StateFlow<Boolean> = repository.hasCompletedOnboarding()

    val isLoggedIn: StateFlow<Boolean> = repository.isLoggedIn()

    val themeMode: StateFlow<String> = repository.getThemeMode()
    val notificationsEnabled: StateFlow<Boolean> = repository.getNotificationsEnabled()
    val biometricEnabled: StateFlow<Boolean> = repository.getBiometricEnabled()

    val cards: StateFlow<List<PaymentCard>> = repository.getCards()
    val upiMethods: StateFlow<List<UpiMethod>> = repository.getUpiMethods()

    private val _loginMobileNumber = MutableStateFlow("")
    val loginMobileNumber: StateFlow<String> = _loginMobileNumber.asStateFlow()

    // Events State from Repository
    private val _eventsListFlow = MutableStateFlow<List<Event>>(DummyData.events)
    val eventsList: List<Event> get() = _eventsListFlow.value
    val categoriesList: List<String> = DummyData.categories

    // Pull to Refresh state
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedPriceRangeMax = MutableStateFlow(2000)
    val selectedPriceRangeMax: StateFlow<Int> = _selectedPriceRangeMax.asStateFlow()

    private val _selectedLocation = MutableStateFlow("Mumbai")
    val selectedLocation: StateFlow<String> = _selectedLocation.asStateFlow()

    // Wishlist (Saved event IDs)
    private val _wishlistedIds = MutableStateFlow<Set<String>>(emptySet())
    val wishlistedIds: StateFlow<Set<String>> = _wishlistedIds.asStateFlow()

    // Bookings History
    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    // Active User Profile
    private val _currentUser = MutableStateFlow(
        User(
            name = "Alex Mercer",
            email = "alex.mercer@gmail.com",
            phone = "+91 98765 43210",
            profileImage = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80"
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Active Booking Details (for Ticket Selection & Checkout flow)
    private val _activeBookingEvent = MutableStateFlow<Event?>(null)
    val activeBookingEvent: StateFlow<Event?> = _activeBookingEvent.asStateFlow()

    private val _activeBookingType = MutableStateFlow("General Admission")
    val activeBookingType: StateFlow<String> = _activeBookingType.asStateFlow()

    private val _activeBookingQuantity = MutableStateFlow(1)
    val activeBookingQuantity: StateFlow<Int> = _activeBookingQuantity.asStateFlow()

    // Customer info inputs for Checkout
    val checkoutName = MutableStateFlow("")
    val checkoutPhone = MutableStateFlow("")
    val checkoutEmail = MutableStateFlow("")

    // Last successful booking for Success Screen
    private val _lastCreatedBooking = MutableStateFlow<Booking?>(null)
    val lastCreatedBooking: StateFlow<Booking?> = _lastCreatedBooking.asStateFlow()

    init {
        // Collect reactive flows from Database/Repository
        viewModelScope.launch {
            repository.getEvents().collectLatest {
                _eventsListFlow.value = it
            }
        }

        viewModelScope.launch {
            repository.getWishlist().collectLatest {
                _wishlistedIds.value = it
            }
        }

        viewModelScope.launch {
            repository.getBookings().collectLatest {
                _bookings.value = it
            }
        }

        viewModelScope.launch {
            repository.getUser().collectLatest { dbUser ->
                if (dbUser != null) {
                    _currentUser.value = dbUser
                    // Sync checkout fields when profile changes
                    checkoutName.value = dbUser.name
                    checkoutPhone.value = dbUser.phone
                    checkoutEmail.value = dbUser.email
                } else {
                    // Seed initial user profile in local database
                    repository.saveUser(_currentUser.value)
                }
            }
        }
    }

    // Refresh Sync (Mock network)
    fun refreshEvents() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repository.refreshEvents()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    // Auth Actions
    fun completeOnboarding() {
        repository.setOnboardingCompleted(true)
    }

    fun setMobileNumber(phone: String) {
        _loginMobileNumber.value = phone
    }

    fun login() {
        repository.setLoggedIn(true)
        // Initialize checkout inputs from active profile session
        checkoutName.value = _currentUser.value.name
        checkoutPhone.value = _currentUser.value.phone
        checkoutEmail.value = _currentUser.value.email
    }

    fun logout() {
        repository.setLoggedIn(false)
        _loginMobileNumber.value = ""
    }

    // Search Actions
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun setLocation(city: String) {
        _selectedLocation.value = city
    }

    fun updatePriceFilter(max: Int) {
        _selectedPriceRangeMax.value = max
    }

    // Wishlist Actions
    fun toggleWishlist(eventId: String) {
        repository.toggleWishlist(eventId)
    }

    // Booking Actions
    fun startBooking(event: Event) {
        _activeBookingEvent.value = event
        _activeBookingType.value = "General Admission"
        _activeBookingQuantity.value = 1
        checkoutName.value = _currentUser.value.name
        checkoutPhone.value = _currentUser.value.phone
        checkoutEmail.value = _currentUser.value.email
    }

    fun updateBookingTicketType(type: String) {
        _activeBookingType.value = type
    }

    fun incrementQuantity() {
        if (_activeBookingQuantity.value < 10) {
            _activeBookingQuantity.value += 1
        }
    }

    fun decrementQuantity() {
        if (_activeBookingQuantity.value > 1) {
            _activeBookingQuantity.value -= 1
        }
    }

    fun getTicketPriceForType(type: String): Int {
        val basePrice = _activeBookingEvent.value?.price ?: 0
        return when (type) {
            "Premium" -> basePrice + 500
            "VIP" -> basePrice + 1500
            else -> basePrice
        }
    }

    fun calculateTotalBookingAmount(): Int {
        val unitPrice = getTicketPriceForType(_activeBookingType.value)
        val subtotal = unitPrice * _activeBookingQuantity.value
        val platformFee = 40
        val gst = (subtotal * 0.18).toInt()
        return subtotal + platformFee + gst
    }

    fun getBreakdown(): Triple<Int, Int, Int> {
        val unitPrice = getTicketPriceForType(_activeBookingType.value)
        val subtotal = unitPrice * _activeBookingQuantity.value
        val platformFee = 40
        val gst = (subtotal * 0.18).toInt()
        return Triple(subtotal, platformFee, gst)
    }

    fun confirmBooking() {
        val event = _activeBookingEvent.value ?: return
        val total = calculateTotalBookingAmount()
        val bookingId = "EVT-${(100000..999999).random()}"

        val newBooking = Booking(
            id = bookingId,
            event = event,
            ticketType = _activeBookingType.value,
            quantity = _activeBookingQuantity.value,
            totalPrice = total,
            customerName = checkoutName.value,
            customerPhone = checkoutPhone.value,
            customerEmail = checkoutEmail.value,
            bookingTime = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()),
            qrCodeToken = "$bookingId-${checkoutName.value.replace(" ", "")}-${_activeBookingType.value}",
            isCompleted = false // Upcoming
        )

        // Write directly to local DB repository flows
        repository.addBooking(newBooking)
        _lastCreatedBooking.value = newBooking

        _activeBookingEvent.value = null
    }

    fun clearLastCreatedBooking() {
        _lastCreatedBooking.value = null
    }

    // Profile Actions
    fun updateProfile(name: String, email: String, phone: String) {
        val updated = _currentUser.value.copy(
            name = name,
            email = email,
            phone = phone
        )
        repository.saveUser(updated)
    }

    // Settings & Payment Methods Actions
    fun setThemeMode(mode: String) {
        repository.setThemeMode(mode)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        repository.setNotificationsEnabled(enabled)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        repository.setBiometricEnabled(enabled)
    }

    fun addCard(cardNumber: String, cardHolder: String, expiryDate: String, cardType: String) {
        val last4 = if (cardNumber.length >= 4) cardNumber.takeLast(4) else cardNumber
        val obfuscated = "•••• •••• •••• $last4"
        val newCard = PaymentCard(
            id = "c-${(1000..9999).random()}",
            cardNumber = obfuscated,
            cardHolder = cardHolder,
            expiryDate = expiryDate,
            cardType = cardType
        )
        repository.addCard(newCard)
    }

    fun deleteCard(cardId: String) {
        repository.deleteCard(cardId)
    }

    fun addUpiMethod(upiId: String, provider: String) {
        val newUpi = UpiMethod(
            id = "u-${(1000..9999).random()}",
            upiId = upiId,
            provider = provider
        )
        repository.addUpiMethod(newUpi)
    }

    fun deleteUpiMethod(upiId: String) {
        repository.deleteUpiMethod(upiId)
    }
}
