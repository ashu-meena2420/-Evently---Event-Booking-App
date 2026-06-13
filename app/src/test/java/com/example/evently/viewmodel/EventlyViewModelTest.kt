package com.example.evently.viewmodel

import android.app.Application
import com.example.evently.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class EventlyViewModelTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeApplication: Application
    private lateinit var viewModel: EventlyViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Subclass Application to redirect filesDir to a JVM temporary directory
        val filesDir = tempFolder.newFolder("files")
        fakeApplication = object : Application() {
            override fun getFilesDir(): File {
                return filesDir
            }
        }

        viewModel = EventlyViewModel(fakeApplication)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testGetTicketPriceForType() = runTest(testDispatcher) {
        val event = Event(
            id = "e1",
            name = "Test Event",
            description = "Desc",
            imageUrl = "",
            date = "Oct 24, 2026",
            time = "06:30 PM",
            venue = "Venue",
            price = 1000,
            rating = 4.8,
            category = "Concerts",
            organizer = "Org",
            highlights = emptyList(),
            gallery = emptyList()
        )

        viewModel.startBooking(event)
        
        // General Admission price should equal base price
        assertEquals(1000, viewModel.getTicketPriceForType("General Admission"))
        
        // Premium price = base + 500
        assertEquals(1500, viewModel.getTicketPriceForType("Premium"))
        
        // VIP price = base + 1500
        assertEquals(2500, viewModel.getTicketPriceForType("VIP"))
    }

    @Test
    fun testQuantityIncrementDecrement() = runTest(testDispatcher) {
        val event = Event(
            id = "e1",
            name = "Test Event",
            description = "Desc",
            imageUrl = "",
            date = "Oct 24, 2026",
            time = "06:30 PM",
            venue = "Venue",
            price = 1000,
            rating = 4.8,
            category = "Concerts",
            organizer = "Org",
            highlights = emptyList(),
            gallery = emptyList()
        )

        viewModel.startBooking(event)
        assertEquals(1, viewModel.activeBookingQuantity.value)

        // Increment should work up to 10
        viewModel.incrementQuantity()
        assertEquals(2, viewModel.activeBookingQuantity.value)

        viewModel.decrementQuantity()
        assertEquals(1, viewModel.activeBookingQuantity.value)

        // Decrement below 1 should be ignored
        viewModel.decrementQuantity()
        assertEquals(1, viewModel.activeBookingQuantity.value)
    }

    @Test
    fun testCalculateTotalBookingAmount() = runTest(testDispatcher) {
        val event = Event(
            id = "e1",
            name = "Test Event",
            description = "Desc",
            imageUrl = "",
            date = "Oct 24, 2026",
            time = "06:30 PM",
            venue = "Venue",
            price = 1000,
            rating = 4.8,
            category = "Concerts",
            organizer = "Org",
            highlights = emptyList(),
            gallery = emptyList()
        )

        viewModel.startBooking(event)
        viewModel.updateBookingTicketType("General Admission") // Price = 1000

        // Subtotal = 1000 * 1 = 1000
        // Platform fee = 40
        // GST = 18% of 1000 = 180
        // Total = 1000 + 40 + 180 = 1220
        assertEquals(1220, viewModel.calculateTotalBookingAmount())
    }

    @Test
    fun testThemeModeChange() = runTest(testDispatcher) {
        viewModel.setThemeMode("DARK")
        // Run any scheduled coroutine jobs on the dispatcher
        testScheduler.advanceUntilIdle()

        val mode = viewModel.themeMode.value
        assertEquals("DARK", mode)
    }
}
