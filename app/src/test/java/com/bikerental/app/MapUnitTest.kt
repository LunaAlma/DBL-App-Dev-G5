package com.bikerental.app

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.bikerental.app.data.model.Bike
import com.bikerental.app.data.model.User
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.data.repositories.UserRepository
import com.bikerental.app.ui.map.MapViewModel
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.navigation.Navigator
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

//class MapUnitTest {
//
//    private lateinit var viewModel: MapViewModel
//    private val mockNavigator: Navigator = mockk(relaxed = true)
//    private val mockSavedStateHandle: SavedStateHandle = mockk()
//    private val mockAuthRepository: AuthRepository = mockk()
//    private val mockBikeRepository: BikeRepository = mockk()
//    private val mockUserRepository: UserRepository = mockk()
//
//    private val testDispatcher = StandardTestDispatcher()
//    private val testScope = TestScope(testDispatcher)
//
//    @Before
//    fun setup() {
//        Dispatchers.setMain(testDispatcher)
//
//        coEvery { mockSavedStateHandle.get<String>("bikeId") } returns "bike1"
//
//        viewModel = MapViewModel(
//            navigator = mockNavigator,
//            savedStateHandle = mockSavedStateHandle,
//            authRepository = mockAuthRepository,
//            bikeRepository = mockBikeRepository,
//            userRepository = mockUserRepository
//        )
//    }
//
//    @After
//    fun tearDown() {
//        Dispatchers.resetMain()
//    }
//
//    @Test
//    fun `test loadBikeData loads bike data successfully`() = testScope.runTest {
//        val bike = Bike(
//            bikeId = "bike1",
//            ownerId = "owner123",
//            bikeName = "Bike 1",
//            price = 100.0,
//            imageUrl = "http://example.com/bike1.png",
//            location = GeoPoint(37.7749, -122.4194),
//            city = "San Francisco",
//            startTime = Timestamp.now(),
//            endTime = Timestamp.now()
//        )
//
//        coEvery { mockBikeRepository.getBikes() } returns flowOf(listOf(bike))
//
//        viewModel.loadBikeData()
//        testScheduler.advanceUntilIdle()
//
//        assertNotNull(viewModel.addedBike.value)
//        assertEquals(bike, viewModel.addedBike.value)
//    }
//
//    @Test
//    fun `test fetchAvailableBikes updates available bikes list`() = testScope.runTest {
//        val bike1 = Bike(
//            bikeId = "bike1",
//            ownerId = "owner1",
//            bikeName = "Bike One",
//            price = 100.0,
//            imageUrl = "http://example.com/bike1.png",
//            location = GeoPoint(37.7749, -122.4194),
//            city = "San Francisco",
//            startTime = Timestamp.now(),
//            endTime = Timestamp.now()
//        )
//        val bike2 = Bike(
//            bikeId = "bike2",
//            ownerId = "owner2",
//            bikeName = "Bike Two",
//            price = 120.0,
//            imageUrl = "http://example.com/bike2.png",
//            location = GeoPoint(37.7749, -122.4194),
//            city = "Los Angeles",
//            startTime = Timestamp.now(),
//            endTime = Timestamp.now()
//        )
//
//        val startTime = Timestamp.now()
//        val endTime = Timestamp.now()
//
//        coEvery { mockBikeRepository.getAvailableBikes2(startTime, endTime) } returns flowOf(listOf(bike1, bike2))
//
//        viewModel.fetchAvailableBikes(startTime, endTime)
//        testScheduler.advanceUntilIdle()
//
//        val availableBikes = viewModel.availableBikes.value
//        assertEquals(2, availableBikes.size)
//        assertEquals("Bike One", availableBikes[0].bikeName)
//        assertEquals("Bike Two", availableBikes[1].bikeName)
//    }
//
//    @Test
//    fun `test ownerDetails returns null when owner ID is not set`() = testScope.runTest {
//        testScheduler.advanceUntilIdle()
//        val ownerDetails = viewModel.ownerDetails.value
//        assertNull(ownerDetails)
//    }
//
//    @Test
//    fun `test updateBikeLocation updates location successfully`() = testScope.runTest {
//        val bikeId = "bike1"
//        val newLocation = LatLng(37.7749, -122.4194)
//
//        coEvery { mockBikeRepository.updateBikeLocation(bikeId, newLocation) } just Runs
//
//        viewModel.updateBikeLocation(bikeId, newLocation)
//        testScheduler.advanceUntilIdle()
//
//        coVerify { mockBikeRepository.updateBikeLocation(bikeId, newLocation) }
//    }
//
//    @Test
//    fun `test goToMap navigates successfully`() = testScope.runTest {
//        viewModel.goToMap()
//        testScheduler.advanceUntilIdle()
//
//        coVerify { mockNavigator.navigateTo(Destination.Home.Map.route) }
//    }
//
//    @Test
//    fun `test onSearchBarClick navigates to search`() = testScope.runTest {
//        viewModel.onSearchBarClick()
//        testScheduler.advanceUntilIdle()
//
//        coVerify { mockNavigator.navigateTo(Destination.Home.Search.route) }
//    }
//
//     Uncomment if needed and mock getUserById
//    @Test
//    fun `test ownerDetails fetches user data successfully`() = testScope.runTest {
//        val uid = "user123"
//        val user = User(uid, "John Doe", "john.doe@example.com")
//        coEvery { mockUserRepository.getUserById(uid) } returns flowOf(user)
//
//        viewModel.setOwnerId(uid)
//        testScheduler.advanceUntilIdle()
//
//        val ownerDetails = viewModel.ownerDetails.value
//        assertNotNull(ownerDetails)
//        assertEquals(uid, ownerDetails?.uid)
//    }
//}
//
