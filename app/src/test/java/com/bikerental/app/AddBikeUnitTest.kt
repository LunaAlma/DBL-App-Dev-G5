package com.bikerental.app.ui.create

import android.net.Uri
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.ui.navigation.Navigator
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
//import org.mockito.Mockito.*
//import org.mockito.kotlin.doReturn
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
//import org.mockito.Mockito.*
//import org.mockito.junit.MockitoJUnit
//import org.mockito.junit.MockitoRule
import io.mockk.*



class AddBikeViewModelTest {

    private lateinit var viewModel: AddBikeViewModel
    private val navigator: Navigator = mockk(relaxed = true) // Mock final class
    private val bikeRepository: BikeRepository = mockk(relaxed = true)
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private val firebaseStorage: FirebaseStorage = mockk(relaxed = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        viewModel = AddBikeViewModel(navigator, bikeRepository, authRepository, firebaseStorage)
    }

    @Test
    fun `test addBikeImage with valid URI`() = runTest {
        val bikeUri = mockk<Uri>()
        val imageUrl = "https://fake-url.com"

        // Mock the suspending method from BikeRepository
        coEvery { bikeRepository.addBikeImage(bikeUri) } returns Result.success(imageUrl)

        // Perform the action that calls the suspending method
        viewModel.onBikeImageChange(bikeUri)
        viewModel.addBike()

        // Verify that the addBikeImage method was called
        coVerify { bikeRepository.addBikeImage(bikeUri) }
    }

//    @Test
//    fun `test bike repository interaction` () = runTest {
//        val bikeUri = mockk<Uri>()
//        every { bikeRepository.addBikeImage(bikeUri) } returns Result.success("https://fake-url.com")
//
//        viewModel.onBikeImageChange(bikeUri)
//        viewModel.addBike()
//
//        verify { bikeRepository.addBikeImage(bikeUri) } // Verify method was called
//    }


    @Test
    fun `test onBikeImageChange updates the image URI`() {
        val testUri = Uri.parse("test://image-uri")
        viewModel.onBikeImageChange(testUri)

        assertEquals(testUri, viewModel.bikeImageUri.value)
    }

    @Test
    fun `test onBikeNameChange updates the bike name`() {
        val testName = "Unittestbike"
        viewModel.onBikeNameChange(testName)

        assertEquals(testName, viewModel.bikeName.value)
    }

    @Test
    fun `test onBikePriceChange updates the bike price`() {
        val testPrice = "3.00"
        viewModel.onBikePriceChange(testPrice)

        assertEquals(testPrice, viewModel.bikePrice.value)
    }

    @Test
    fun `test onBikeCityChange updates the bike city`() {
        val testCity = "Den Haag"
        viewModel.onBikeCityChange(testCity)

        assertEquals(testCity, viewModel.city.value)
    }

    @Test
    fun `test onStartDateSelected updates the start date`() {
        val testDate = LocalDate.of(2025, 4, 1)
        viewModel.onStartDateSelected(testDate)

        val expectedTimestamp = Timestamp(testDate.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond, 0)
        assertEquals(expectedTimestamp, viewModel.selectedStartDate.value)
    }

    @Test
    fun `test onEndDateSelected updates the end date`() {
        val testDate = LocalDate.of(2025, 4, 10)
        viewModel.onEndDateSelected(testDate)

        val expectedTimestamp = Timestamp(testDate.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond, 0)
        assertEquals(expectedTimestamp, viewModel.selectedEndDate.value)
    }

    @Test
    fun `test validate with invalid bike name`() {
        // Simulate an invalid bike name
        viewModel.onBikeNameChange("")

        val isValid = viewModel.validate()
        assertFalse(isValid)
        assertEquals("Bike Name length should be at least 6", viewModel.bikeNameError.value)
    }

    @Test
    fun `test validate with valid inputs`() {
        // Simulate valid inputs
        viewModel.onBikeNameChange("Unittestbike")
        viewModel.onBikePriceChange("15.00")
        viewModel.onBikeCityChange("New York")
        viewModel.onStartDateSelected(LocalDate.of(2025, 4, 1))
        viewModel.onEndDateSelected(LocalDate.of(2025, 4, 10))

        val isValid = viewModel.validate()

        assertTrue(isValid)
        assertTrue(viewModel.bikeNameError.value.isEmpty())
    }

    @Test
    fun `test addBike with valid input triggers repository method`() = runTest {
        // Test data
        val bikeName = "Unittestbike"
        val bikePrice = "15.00"
        val city = "Rotterdam"
        val startDate = LocalDate.of(2025, 4, 1)
        val endDate = LocalDate.of(2025, 4, 10)
        val imageUri = Uri.parse("test://image-uri")

        // Simulate user input
        viewModel.onBikeNameChange(bikeName)
        viewModel.onBikePriceChange(bikePrice)
        viewModel.onBikeCityChange(city)
        viewModel.onStartDateSelected(startDate)
        viewModel.onEndDateSelected(endDate)
        viewModel.onBikeImageChange(imageUri)

        // Mocking the BikeRepository's addBikeImage suspending function
        val mockUrl = "https://test.com/image.jpg"
        coEvery { bikeRepository.addBikeImage(any()) } returns Result.success(mockUrl)

        // Mocking current user (FirebaseUser)
        val mockUser = mockk<FirebaseUser> {
            every { uid } returns "test-user-id"
        }
        coEvery { authRepository.getCurrentUser } returns mockUser

        // Call the addBike method (this will trigger suspending functions)
        viewModel.addBike()

        // Verify that addBikeImage and addBike were called
        coVerify { bikeRepository.addBikeImage(any()) }
        coVerify {
            bikeRepository.addBike(
                any(),  // UUID is randomly generated in the function
                "test-user-id",
                bikePrice.toDouble(),
                bikeName,
                city,
                mockUrl,  // The mocked URL
                any(),    // The start date (converted to Timestamp)
                any()     // The end date (converted to Timestamp)
            )
        }
    }

    @Test
    fun `test addBike with error calls firebaseError`() = runTest {
        viewModel.onBikeNameChange("Unittestbike")
        viewModel.onBikePriceChange("7.00")
        viewModel.onBikeCityChange("Eindhoven")
        viewModel.onStartDateSelected(LocalDate.of(2025, 4, 1))
        viewModel.onEndDateSelected(LocalDate.of(2025, 4, 10))

        coEvery { bikeRepository.addBikeImage(any()) } returns Result.failure(Exception("Upload failed"))

        viewModel.addBike()

        assertEquals("Profile image upload failed", viewModel.firebaseError.value)
    }
}
