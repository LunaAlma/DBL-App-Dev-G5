package com.bikerental.app.ui.currentrentals

import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.model.Rental
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.ui.navigation.Navigator
import com.google.firebase.Timestamp
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class CurrentRentalsViewModelTest {

    private lateinit var viewModel: CurrentRentalsViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val mockUserRepo: UserRepository = mockk(relaxUnitFun = true)
    private val mockAuthRepo: AuthRepository = mockk()
    private val mockBikeRepo: BikeRepository = mockk()
    private val mockNavigator: Navigator = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { mockBikeRepo.getBikeDetailsById(any()) } returns Bike(
            bikeId = "testBike",
            bikeName = "Test Bike",
            city = "Amsterdam",
            ownerId = "owner123",
            price = 15.0,
            imageUrl = "",
            startTime = Timestamp.now(),
            endTime = Timestamp.now()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCurrentRentals should populate active rentals`() = runTest {
        // Arrange
        val testRentals = listOf(
            Rental(
                id = "1",
                bikeId = "bike1",
                renterId = "user123",
                ownerId = "owner123",
                status = "active",
                startTime = Timestamp.now(),
                endTime = Timestamp(Date().apply { time += 3600000 })
            )
        )

        coEvery { mockAuthRepo.getCurrentUser?.uid } returns "user123"
        every { mockUserRepo.getUserRentals(any()) } returns flowOf(testRentals)

        // Act
        viewModel = CurrentRentalsViewModel(mockUserRepo, mockAuthRepo, mockBikeRepo, mockNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(1, viewModel.currentRentalDisplays.value.size)
        assertEquals("Test Bike", viewModel.currentRentalDisplays.value.first().bikeName)
    }

    @Test
    fun `should filter out non-active rentals`() = runTest {
        // Arrange
        val testRentals = listOf(
            Rental(
                id = "1",
                bikeId = "bike1",
                renterId = "user123",
                ownerId = "owner123",
                status = "active",
                startTime = Timestamp.now(),
                endTime = Timestamp.now()
            ),
            Rental(
                id = "2",
                bikeId = "bike2",
                renterId = "user123",
                ownerId = "owner123",
                status = "completed",
                startTime = Timestamp.now(),
                endTime = Timestamp.now()
            )
        )

        coEvery { mockAuthRepo.getCurrentUser?.uid } returns "user123"
        every { mockUserRepo.getUserRentals(any()) } returns flowOf(testRentals)

        // Act
        viewModel = CurrentRentalsViewModel(mockUserRepo, mockAuthRepo, mockBikeRepo, mockNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(1, viewModel.currentRentalDisplays.value.size)
        assertEquals("active", viewModel.currentRentalDisplays.value.first().status)
    }

    @Test
    fun `should handle unauthenticated user`() = runTest {
        // Arrange
        coEvery { mockAuthRepo.getCurrentUser } returns null

        // Act
        viewModel = CurrentRentalsViewModel(mockUserRepo, mockAuthRepo, mockBikeRepo, mockNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.currentRentalDisplays.value.isEmpty())
    }

    @Test
    fun `should handle repository errors`() = runTest {
        // Arrange
        coEvery { mockAuthRepo.getCurrentUser?.uid } returns "user123"
        every { mockUserRepo.getUserRentals(any()) } returns flow { throw Exception("Test error") }

        // Act
        viewModel = CurrentRentalsViewModel(mockUserRepo, mockAuthRepo, mockBikeRepo, mockNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.currentRentalDisplays.value.isEmpty())
    }
}