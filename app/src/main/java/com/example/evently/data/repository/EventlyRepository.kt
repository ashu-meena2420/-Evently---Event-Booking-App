package com.example.evently.data.repository

import com.example.evently.data.DummyData
import com.example.evently.data.local.EventlyDatabase
import com.example.evently.model.Booking
import com.example.evently.model.Event
import com.example.evently.model.User
import com.example.evently.model.PaymentCard
import com.example.evently.model.UpiMethod
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

interface EventlyRepository {
    fun getEvents(): Flow<List<Event>>
    fun getWishlist(): StateFlow<Set<String>>
    fun getBookings(): StateFlow<List<Booking>>
    fun getUser(): StateFlow<User?>
    fun isLoggedIn(): StateFlow<Boolean>
    fun setLoggedIn(value: Boolean)
    fun hasCompletedOnboarding(): StateFlow<Boolean>
    fun setOnboardingCompleted(value: Boolean)
    fun toggleWishlist(eventId: String)
    fun addBooking(booking: Booking)
    fun saveUser(user: User)
    suspend fun refreshEvents(): List<Event>

    fun getThemeMode(): StateFlow<String>
    fun setThemeMode(mode: String)
    fun getNotificationsEnabled(): StateFlow<Boolean>
    fun setNotificationsEnabled(enabled: Boolean)
    fun getBiometricEnabled(): StateFlow<Boolean>
    fun setBiometricEnabled(enabled: Boolean)

    fun getCards(): StateFlow<List<PaymentCard>>
    fun addCard(card: PaymentCard)
    fun deleteCard(cardId: String)
    fun getUpiMethods(): StateFlow<List<UpiMethod>>
    fun addUpiMethod(upi: UpiMethod)
    fun deleteUpiMethod(upiId: String)
}

class DefaultEventlyRepository(private val database: EventlyDatabase) : EventlyRepository {

    private val _eventsFlow = MutableStateFlow<List<Event>>(DummyData.events)

    override fun getEvents(): Flow<List<Event>> = _eventsFlow

    override fun getWishlist(): StateFlow<Set<String>> = database.wishlistFlow

    override fun getBookings(): StateFlow<List<Booking>> = database.bookingsFlow

    override fun getUser(): StateFlow<User?> = database.userFlow

    override fun isLoggedIn(): StateFlow<Boolean> = database.isLoggedInFlow

    override fun setLoggedIn(value: Boolean) {
        database.setLoggedIn(value)
    }

    override fun hasCompletedOnboarding(): StateFlow<Boolean> = database.hasCompletedOnboardingFlow

    override fun setOnboardingCompleted(value: Boolean) {
        database.setOnboardingCompleted(value)
    }

    override fun toggleWishlist(eventId: String) {
        database.toggleWishlist(eventId)
    }

    override fun addBooking(booking: Booking) {
        database.saveBooking(booking)
    }

    override fun saveUser(user: User) {
        database.saveUserProfile(user)
    }

    override suspend fun refreshEvents(): List<Event> {
        // Simulate a real-world network delay of 1.5 seconds to pull from production API
        delay(1500)
        
        // Randomize the order of recommendations or prices slightly to simulate dynamic updates
        val refreshed = DummyData.events.shuffled()
        _eventsFlow.value = refreshed
        return refreshed
    }

    override fun getThemeMode(): StateFlow<String> = database.themeModeFlow

    override fun setThemeMode(mode: String) {
        database.setThemeMode(mode)
    }

    override fun getNotificationsEnabled(): StateFlow<Boolean> = database.notificationsEnabledFlow

    override fun setNotificationsEnabled(enabled: Boolean) {
        database.setNotificationsEnabled(enabled)
    }

    override fun getBiometricEnabled(): StateFlow<Boolean> = database.biometricEnabledFlow

    override fun setBiometricEnabled(enabled: Boolean) {
        database.setBiometricEnabled(enabled)
    }

    override fun getCards(): StateFlow<List<PaymentCard>> = database.cardsFlow

    override fun addCard(card: PaymentCard) {
        database.addCard(card)
    }

    override fun deleteCard(cardId: String) {
        database.deleteCard(cardId)
    }

    override fun getUpiMethods(): StateFlow<List<UpiMethod>> = database.upiMethodsFlow

    override fun addUpiMethod(upi: UpiMethod) {
        database.addUpiMethod(upi)
    }

    override fun deleteUpiMethod(upiId: String) {
        database.deleteUpiMethod(upiId)
    }
}

