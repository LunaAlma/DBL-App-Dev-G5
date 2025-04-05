package com.bikerental.app

import com.bikerental.app.data.model.PastRentalDisplay
import com.bikerental.app.data.model.Rental
import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.ui.navigation.Navigator
import com.bikerental.app.ui.pastrentals.PastRentalsViewModel
import com.google.firebase.Timestamp
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle

@OptIn(ExperimentalCoroutinesApi::class)
class PastRentalsViewModelTest {

    private lateinit var viewModel: PastRentalsViewModel
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private val bikeRepository: BikeRepository = mockk(relaxed = true)
    private val navigator: Navigator = mockk(relaxed = true)

    @Before
    fun setup() {
        // Mock Log.d() and Log.e() to prevent crashes
        mockkStatic("android.util.Log")
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0

        viewModel = PastRentalsViewModel(userRepository, authRepository, bikeRepository, navigator)
    }

    @Test
    fun `test loadPastRentals fetches completed rentals and updates state`() = runTest {
        // Mock authenticated user
        every { authRepository.getCurrentUser?.uid } returns "user123"

        // Mock user rentals
        val mockRentals = listOf(
            Rental(
                id = "rental1",
                bikeId = "bike1",
                renterId = "user123",
                ownerId = "owner456",
                status = "completed",
                startTime = Timestamp(1000, 0),
                endTime = Timestamp(2000, 0)
            )
        )
        every { userRepository.getUserRentals("user123") } returns flow { emit(mockRentals) }

        // Mock bike details (MUST use `coEvery` for suspend functions)
        coEvery { bikeRepository.getBikeDetailsById("bike1") } returns mockk {
            every { bikeName } returns "Mountain Bike"
            every { city } returns "Amsterdam"
        }

        // Act
        viewModel.loadPastRentals()

        // Assert
        val expectedDisplay = listOf(
            PastRentalDisplay(
                bikeName = "Mountain Bike",
                bikeCity = "Amsterdam",
                startTime = Timestamp(1000, 0),
                endTime = Timestamp(2000, 0),
                status = "completed"
            )
        )

        viewModel.loadPastRentals()

        advanceUntilIdle()
        assertEquals(expectedDisplay, viewModel.pastRentalDisplays.value)

        // Verify interactions
        coVerify { bikeRepository.getBikeDetailsById("bike1") }
    }

    @Test
    fun `test loadPastRentals when user is not logged in`() = runTest {
        // Mock no user
        every { authRepository.getCurrentUser?.uid } returns null

        // Act
        viewModel.loadPastRentals()

        // Assert: past rentals should remain empty
        assertEquals(emptyList<PastRentalDisplay>(), viewModel.pastRentalDisplays.value)
    }

    @Test
    fun `test loadPastRentals handles empty rental list`() = runTest {
        // Mock authenticated user
        every { authRepository.getCurrentUser?.uid } returns "user123"

        // Mock empty rental list
        every { userRepository.getUserRentals("user123") } returns flow { emit(emptyList()) }

        // Act
        viewModel.loadPastRentals()

        // Assert: no past rentals should be set
        assertEquals(emptyList<PastRentalDisplay>(), viewModel.pastRentalDisplays.value)
    }

    @Test
    fun `test loadPastRentals handles rental fetch error`() = runTest {
        // Mock authenticated user
        every { authRepository.getCurrentUser?.uid } returns "user123"

        // Mock rental fetch failure
        every { userRepository.getUserRentals("user123") } returns flow { throw Exception("Database error") }

        // Act
        viewModel.loadPastRentals()

        // Assert: past rentals should remain empty
        assertEquals(emptyList<PastRentalDisplay>(), viewModel.pastRentalDisplays.value)
    }
}