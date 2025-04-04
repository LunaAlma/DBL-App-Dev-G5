package com.bikerental.app
//
//import android.util.Log
//import com.bikerental.app.data.model.Bike
//import com.bikerental.app.data.model.PastRentalDisplay
//import com.bikerental.app.data.model.Rental
//import com.bikerental.app.data.repositories.UserRepository
//import com.bikerental.app.data.repositories.AuthRepository
//import com.bikerental.app.data.repositories.BikeRepository
//import com.bikerental.app.ui.currentrentals.CurrentRentalsViewModel
//import com.bikerental.app.ui.navigation.Navigator
//import com.google.firebase.Timestamp
//import io.mockk.*
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.test.runTest
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//
//@OptIn(ExperimentalCoroutinesApi::class)
//class CurrentRentalsViewModelTest {
//
//    private lateinit var viewModel: CurrentRentalsViewModel
//    private val userRepository: UserRepository = mockk()
//    private val authRepository: AuthRepository = mockk()
//    private val bikeRepository: BikeRepository = mockk()
//    private val navigator: Navigator = mockk(relaxed = true)
//
//    @Before
//    fun setup() {
//        mockkStatic(Log::class)
//        every { Log.e(any(), any()) } returns 0
//
//        viewModel = CurrentRentalsViewModel(userRepository, authRepository, bikeRepository, navigator)
//    }
//
//    @After
//    fun tearDown() {
//        unmockkStatic(Log::class)
//    }
//
//    @Test
//    fun `loadCurrentRentals logs error if user not logged in`() = runTest {
//        coEvery { authRepository.getCurrentUser?.uid } returns null
//
//        viewModel.loadCurrentRentals()
//
//        coVerify { Log.e("CurrentRentals", "User not logged in") }
//    }
//
//    @Test
//    fun `loadCurrentRentals handles exception from repository`() = runTest {
//        val userId = "user123"
//        coEvery { authRepository.getCurrentUser?.uid } returns userId
//        coEvery { userRepository.getUserRentals(userId) } returns flow { throw Exception("Some error") }
//
//        viewModel.loadCurrentRentals()
//
//        coVerify { Log.e("CurrentRentals", "Error: Some error") }
//    }
//
//    @Test
//    fun `loadCurrentRentals returns empty list when no active rentals`() = runTest {
//        val userId = "user123"
//        coEvery { authRepository.getCurrentUser?.uid } returns userId
//        coEvery { userRepository.getUserRentals(userId) } returns flowOf(
//            listOf(
//                Rental("bike1", Timestamp(0, 0), Timestamp(0, 0), "completed"),
//                Rental("bike2", Timestamp(0, 0), Timestamp(0, 0), "completed")
//            )
//        )
//
//        viewModel.loadCurrentRentals()
//
//        val result = viewModel.currentRentalDisplays.first()
//        assertTrue(result.isEmpty())
//    }
//
//    @Test
//    fun `loadCurrentRentals returns correct display items for active rentals`() = runTest {
//        val userId = "user123"
//        coEvery { authRepository.getCurrentUser?.uid } returns userId
//
//        val rental1 = Rental("bike1", Timestamp(100, 0), Timestamp(200, 0), "active")
//        val rental2 = Rental("bike2", Timestamp(300, 0), Timestamp(400, 0), "active")
//        coEvery { userRepository.getUserRentals(userId) } returns flowOf(listOf(rental1, rental2))
//
//        coEvery { bikeRepository.getBikeDetailsById("bike1") } returns Bike("Bike One", "City A")
//        coEvery { bikeRepository.getBikeDetailsById("bike2") } returns Bike("Bike Two", "City B")
//
//        viewModel.loadCurrentRentals()
//        val result = viewModel.currentRentalDisplays.first()
//
//        assertEquals(2, result.size)
//        assertEquals("Bike One", result[0].bikeName)
//        assertEquals("City A", result[0].bikeCity)
//        assertEquals("Bike Two", result[1].bikeName)
//        assertEquals("City B", result[1].bikeCity)
//    }
//
//    @Test
//    fun `loadCurrentRentals handles multiple rentals with correct data`() = runTest {
//        val userId = "user456"
//        coEvery { authRepository.getCurrentUser?.uid } returns userId
//
//        val rental1 = Rental("bike1", Timestamp(123456, 0), Timestamp(123999, 0), "active")
//        val rental2 = Rental("bike2", Timestamp(124000, 0), Timestamp(124999, 0), "active")
//
//        coEvery { userRepository.getUserRentals(userId) } returns flowOf(listOf(rental1, rental2))
//        coEvery { bikeRepository.getBikeDetailsById("bike1") } returns Bike("Roadster", "Amsterdam")
//        coEvery { bikeRepository.getBikeDetailsById("bike2") } returns Bike("Mountain King", "Rotterdam")
//
//        viewModel.loadCurrentRentals()
//
//        val displays = viewModel.currentRentalDisplays.first()
//        assertEquals(2, displays.size)
//
//        assertEquals("Roadster", displays[0].bikeName)
//        assertEquals("Amsterdam", displays[0].bikeCity)
//
//        assertEquals("Mountain King", displays[1].bikeName)
//        assertEquals("Rotterdam", displays[1].bikeCity)
//    }
//}
