package com.example.evently.data.local

import android.content.Context
import com.example.evently.model.Booking
import com.example.evently.model.Event
import com.example.evently.model.User
import com.example.evently.model.PaymentCard
import com.example.evently.model.UpiMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class EventlyDatabase(private val context: Context) {

    private val dbScope = CoroutineScope(Dispatchers.IO)

    // Private Files
    private val wishlistFile = File(context.filesDir, "wishlist.json")
    private val bookingsFile = File(context.filesDir, "bookings.json")
    private val userFile = File(context.filesDir, "user.json")
    private val authFile = File(context.filesDir, "auth.json")
    private val paymentMethodsFile = File(context.filesDir, "payment_methods.json")

    // Reactive State Flows
    private val _wishlistFlow = MutableStateFlow<Set<String>>(emptySet())
    val wishlistFlow: StateFlow<Set<String>> = _wishlistFlow.asStateFlow()

    private val _bookingsFlow = MutableStateFlow<List<Booking>>(emptyList())
    val bookingsFlow: StateFlow<List<Booking>> = _bookingsFlow.asStateFlow()

    private val _userFlow = MutableStateFlow<User?>(null)
    val userFlow: StateFlow<User?> = _userFlow.asStateFlow()

    private val _isLoggedInFlow = MutableStateFlow<Boolean>(false)
    val isLoggedInFlow: StateFlow<Boolean> = _isLoggedInFlow.asStateFlow()

    private val _hasCompletedOnboardingFlow = MutableStateFlow<Boolean>(false)
    val hasCompletedOnboardingFlow: StateFlow<Boolean> = _hasCompletedOnboardingFlow.asStateFlow()

    private val _themeModeFlow = MutableStateFlow<String>("SYSTEM")
    val themeModeFlow: StateFlow<String> = _themeModeFlow.asStateFlow()

    private val _notificationsEnabledFlow = MutableStateFlow<Boolean>(true)
    val notificationsEnabledFlow: StateFlow<Boolean> = _notificationsEnabledFlow.asStateFlow()

    private val _biometricEnabledFlow = MutableStateFlow<Boolean>(false)
    val biometricEnabledFlow: StateFlow<Boolean> = _biometricEnabledFlow.asStateFlow()

    private val _cardsFlow = MutableStateFlow<List<PaymentCard>>(emptyList())
    val cardsFlow: StateFlow<List<PaymentCard>> = _cardsFlow.asStateFlow()

    private val _upiMethodsFlow = MutableStateFlow<List<UpiMethod>>(emptyList())
    val upiMethodsFlow: StateFlow<List<UpiMethod>> = _upiMethodsFlow.asStateFlow()

    init {
        loadAllData()
    }

    private fun loadAllData() {
        // Load Auth Session & Settings
        if (authFile.exists()) {
            try {
                val json = authFile.readText()
                val obj = JSONObject(json)
                _isLoggedInFlow.value = obj.optBoolean("isLoggedIn", false)
                _hasCompletedOnboardingFlow.value = obj.optBoolean("hasCompletedOnboarding", false)
                _themeModeFlow.value = obj.optString("themeMode", "SYSTEM")
                _notificationsEnabledFlow.value = obj.optBoolean("notificationsEnabled", true)
                _biometricEnabledFlow.value = obj.optBoolean("biometricEnabled", false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Load Wishlist
        if (wishlistFile.exists()) {
            try {
                val json = wishlistFile.readText()
                val array = JSONArray(json)
                val set = mutableSetOf<String>()
                for (i in 0 until array.length()) {
                    set.add(array.getString(i))
                }
                _wishlistFlow.value = set
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Load User Profile
        if (userFile.exists()) {
            try {
                val json = userFile.readText()
                val obj = JSONObject(json)
                _userFlow.value = User(
                    name = obj.getString("name"),
                    email = obj.getString("email"),
                    phone = obj.getString("phone"),
                    profileImage = obj.getString("profileImage")
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // Default user
            _userFlow.value = User(
                name = "Alex Mercer",
                email = "alex.mercer@gmail.com",
                phone = "+91 98765 43210",
                profileImage = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80"
            )
        }

        // Load Bookings
        if (bookingsFile.exists()) {
            try {
                val json = bookingsFile.readText()
                val array = JSONArray(json)
                val list = mutableListOf<Booking>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val eventObj = obj.getJSONObject("event")
                    
                    val event = Event(
                        id = eventObj.getString("id"),
                        name = eventObj.getString("name"),
                        description = eventObj.getString("description"),
                        imageUrl = eventObj.getString("imageUrl"),
                        date = eventObj.getString("date"),
                        time = eventObj.getString("time"),
                        venue = eventObj.getString("venue"),
                        price = eventObj.getInt("price"),
                        rating = eventObj.getDouble("rating"),
                        category = eventObj.getString("category"),
                        organizer = eventObj.getString("organizer"),
                        highlights = jsonArrayToList(eventObj.getJSONArray("highlights")),
                        gallery = jsonArrayToList(eventObj.getJSONArray("gallery"))
                    )

                    list.add(
                        Booking(
                            id = obj.getString("id"),
                            event = event,
                            ticketType = obj.getString("ticketType"),
                            quantity = obj.getInt("quantity"),
                            totalPrice = obj.getInt("totalPrice"),
                            customerName = obj.getString("customerName"),
                            customerPhone = obj.getString("customerPhone"),
                            customerEmail = obj.getString("customerEmail"),
                            bookingTime = obj.getString("bookingTime"),
                            qrCodeToken = obj.getString("qrCodeToken"),
                            isCompleted = obj.getBoolean("isCompleted")
                        )
                    )
                }
                _bookingsFlow.value = list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // Default booking history seed
            val initialList = mutableListOf<Booking>()
            _bookingsFlow.value = initialList
        }

        // Load Payment Methods
        if (paymentMethodsFile.exists()) {
            try {
                val json = paymentMethodsFile.readText()
                val root = JSONObject(json)
                
                // Parse Cards
                val cardsArr = root.optJSONArray("cards")
                val cardsList = mutableListOf<PaymentCard>()
                if (cardsArr != null) {
                    for (i in 0 until cardsArr.length()) {
                        val c = cardsArr.getJSONObject(i)
                        cardsList.add(
                            PaymentCard(
                                id = c.getString("id"),
                                cardNumber = c.getString("cardNumber"),
                                cardHolder = c.getString("cardHolder"),
                                expiryDate = c.getString("expiryDate"),
                                cardType = c.getString("cardType")
                            )
                        )
                    }
                }
                _cardsFlow.value = cardsList

                // Parse UPI Methods
                val upiArr = root.optJSONArray("upiMethods")
                val upiList = mutableListOf<UpiMethod>()
                if (upiArr != null) {
                    for (i in 0 until upiArr.length()) {
                        val u = upiArr.getJSONObject(i)
                        upiList.add(
                            UpiMethod(
                                id = u.getString("id"),
                                upiId = u.getString("upiId"),
                                provider = u.getString("provider")
                            )
                        )
                    }
                }
                _upiMethodsFlow.value = upiList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // Seed default payment methods
            val defaultCards = listOf(
                PaymentCard("c1", "•••• •••• •••• 4589", "Alex Mercer", "12/29", "Visa"),
                PaymentCard("c2", "•••• •••• •••• 8901", "Alex Mercer", "08/31", "Mastercard")
            )
            val defaultUpis = listOf(
                UpiMethod("u1", "alex.mercer@okaxis", "GooglePay"),
                UpiMethod("u2", "9876543210@paytm", "Paytm")
            )
            _cardsFlow.value = defaultCards
            _upiMethodsFlow.value = defaultUpis
            savePaymentMethodsToFile(defaultCards, defaultUpis)
        }
    }

    // Wishlist Operations
    fun toggleWishlist(eventId: String) {
        val current = _wishlistFlow.value.toMutableSet()
        if (current.contains(eventId)) {
            current.remove(eventId)
        } else {
            current.add(eventId)
        }
        _wishlistFlow.value = current
        dbScope.launch {
            try {
                val array = JSONArray()
                current.forEach { array.put(it) }
                wishlistFile.writeText(array.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Booking Operations
    fun saveBooking(booking: Booking) {
        val current = _bookingsFlow.value.toMutableList()
        current.add(0, booking) // Add to top
        _bookingsFlow.value = current
        dbScope.launch {
            saveBookingsToFile(current)
        }
    }

    private fun saveBookingsToFile(list: List<Booking>) {
        try {
            val array = JSONArray()
            list.forEach { booking ->
                val obj = JSONObject()
                obj.put("id", booking.id)
                obj.put("ticketType", booking.ticketType)
                obj.put("quantity", booking.quantity)
                obj.put("totalPrice", booking.totalPrice)
                obj.put("customerName", booking.customerName)
                obj.put("customerPhone", booking.customerPhone)
                obj.put("customerEmail", booking.customerEmail)
                obj.put("bookingTime", booking.bookingTime)
                obj.put("qrCodeToken", booking.qrCodeToken)
                obj.put("isCompleted", booking.isCompleted)

                // Event serialization
                val eventObj = JSONObject()
                eventObj.put("id", booking.event.id)
                eventObj.put("name", booking.event.name)
                eventObj.put("description", booking.event.description)
                eventObj.put("imageUrl", booking.event.imageUrl)
                eventObj.put("date", booking.event.date)
                eventObj.put("time", booking.event.time)
                eventObj.put("venue", booking.event.venue)
                eventObj.put("price", booking.event.price)
                eventObj.put("rating", booking.event.rating)
                eventObj.put("category", booking.event.category)
                eventObj.put("organizer", booking.event.organizer)
                
                val highlightsArray = JSONArray()
                booking.event.highlights.forEach { highlightsArray.put(it) }
                eventObj.put("highlights", highlightsArray)

                val galleryArray = JSONArray()
                booking.event.gallery.forEach { galleryArray.put(it) }
                eventObj.put("gallery", galleryArray)

                obj.put("event", eventObj)
                array.put(obj)
            }
            bookingsFile.writeText(array.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Profile Operations
    fun saveUserProfile(user: User) {
        _userFlow.value = user
        dbScope.launch {
            try {
                val obj = JSONObject()
                obj.put("name", user.name)
                obj.put("email", user.email)
                obj.put("phone", user.phone)
                obj.put("profileImage", user.profileImage)
                userFile.writeText(obj.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        _isLoggedInFlow.value = isLoggedIn
        if (isLoggedIn) {
            _hasCompletedOnboardingFlow.value = true
        }
        saveSettingsToAuthFile()
    }

    fun setOnboardingCompleted(completed: Boolean) {
        _hasCompletedOnboardingFlow.value = completed
        saveSettingsToAuthFile()
    }

    fun setThemeMode(mode: String) {
        _themeModeFlow.value = mode
        saveSettingsToAuthFile()
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabledFlow.value = enabled
        saveSettingsToAuthFile()
    }

    fun setBiometricEnabled(enabled: Boolean) {
        _biometricEnabledFlow.value = enabled
        saveSettingsToAuthFile()
    }

    private fun saveSettingsToAuthFile() {
        dbScope.launch {
            try {
                val obj = if (authFile.exists()) {
                    JSONObject(authFile.readText())
                } else {
                    JSONObject()
                }
                obj.put("isLoggedIn", _isLoggedInFlow.value)
                obj.put("hasCompletedOnboarding", _hasCompletedOnboardingFlow.value)
                obj.put("themeMode", _themeModeFlow.value)
                obj.put("notificationsEnabled", _notificationsEnabledFlow.value)
                obj.put("biometricEnabled", _biometricEnabledFlow.value)
                authFile.writeText(obj.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun savePaymentMethodsToFile(cards: List<PaymentCard>, upis: List<UpiMethod>) {
        dbScope.launch {
            try {
                val root = JSONObject()
                
                val cardsArr = JSONArray()
                cards.forEach { card ->
                    val c = JSONObject()
                    c.put("id", card.id)
                    c.put("cardNumber", card.cardNumber)
                    c.put("cardHolder", card.cardHolder)
                    c.put("expiryDate", card.expiryDate)
                    c.put("cardType", card.cardType)
                    cardsArr.put(c)
                }
                root.put("cards", cardsArr)

                val upiArr = JSONArray()
                upis.forEach { upi ->
                    val u = JSONObject()
                    u.put("id", upi.id)
                    u.put("upiId", upi.upiId)
                    u.put("provider", upi.provider)
                    upiArr.put(u)
                }
                root.put("upiMethods", upiArr)

                paymentMethodsFile.writeText(root.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addCard(card: PaymentCard) {
        val current = _cardsFlow.value.toMutableList()
        current.add(card)
        _cardsFlow.value = current
        savePaymentMethodsToFile(current, _upiMethodsFlow.value)
    }

    fun deleteCard(cardId: String) {
        val current = _cardsFlow.value.toMutableList()
        current.removeAll { it.id == cardId }
        _cardsFlow.value = current
        savePaymentMethodsToFile(current, _upiMethodsFlow.value)
    }

    fun addUpiMethod(upi: UpiMethod) {
        val current = _upiMethodsFlow.value.toMutableList()
        current.add(upi)
        _upiMethodsFlow.value = current
        savePaymentMethodsToFile(_cardsFlow.value, current)
    }

    fun deleteUpiMethod(upiId: String) {
        val current = _upiMethodsFlow.value.toMutableList()
        current.removeAll { it.id == upiId }
        _upiMethodsFlow.value = current
        savePaymentMethodsToFile(_cardsFlow.value, current)
    }

    // Helper functions
    private fun jsonArrayToList(array: JSONArray): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
            list.add(array.getString(i))
        }
        return list
    }
}
