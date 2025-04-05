package com.bikerental.app

import android.net.Uri
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.ui.create.AddBikeViewModel
import com.bikerental.app.ui.navigation.Navigator
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import io.mockk.*
import kotlinx.coroutines.test.runTest


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
        val bikeUri = mockk<Uri>(relaxed = true)
        val imageUrl = "https://fake-url.com"

        viewModel.onBikeNameChange("UnitBike")
        viewModel.onBikePriceChange("12.00")
        viewModel.onBikeCityChange("Amsterdam")
        viewModel.onStartDateSelected(LocalDate.of(2025, 4, 1))
        viewModel.onEndDateSelected(LocalDate.of(2025, 4, 10))
        viewModel.onBikeImageChange(bikeUri)

        coEvery { bikeRepository.addBikeImage(bikeUri) } returns Result.success(imageUrl)
        val mockUser = mockk<FirebaseUser> {
            every { uid } returns "mock-user-id"
        }
        coEvery { authRepository.getCurrentUser } returns mockUser

        // Perform action
        viewModel.addBike()

        // Verify that image upload was attempted
        coVerify { bikeRepository.addBikeImage(bikeUri) }
    }



    @Test
    fun `test bike repository interaction`() = runTest {
        val bikeUri = mockk<Uri>(relaxed = true)

        // Set all required values to pass validation
        viewModel.onBikeNameChange("TestBike")
        viewModel.onBikePriceChange("20.00")
        viewModel.onBikeCityChange("Utrecht")
        viewModel.onStartDateSelected(LocalDate.of(2025, 4, 1))
        viewModel.onEndDateSelected(LocalDate.of(2025, 4, 10))
        viewModel.onBikeImageChange(bikeUri)

        // Mock repository and user
        coEvery { bikeRepository.addBikeImage(bikeUri) } returns Result.success("https://fake-url.com")
        val mockUser = mockk<FirebaseUser> {
            every { uid } returns "test-user-id"
        }
        coEvery { authRepository.getCurrentUser } returns mockUser

        // Act
        viewModel.addBike()

        // Assert that image upload method was called
        coVerify { bikeRepository.addBikeImage(bikeUri) }
    }


    @Test
    fun `test onBikeImageChange updates the image URI`() {
        val testUri = mockk<Uri>(relaxed = true)

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
        val mockUri = mockk<Uri>()

        // Simulate valid inputs
        viewModel.onBikeNameChange("Unittestbike")
        viewModel.onBikePriceChange("15.00")
        viewModel.onBikeCityChange("Eindhoven")
        viewModel.onStartDateSelected(LocalDate.of(2025, 4, 1))
        viewModel.onEndDateSelected(LocalDate.of(2025, 4, 10))
        viewModel.onBikeImageChange(mockUri)

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

        val imageUri = mockk<Uri>(relaxed = true)

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
        coVerify { bikeRepository.addBikeImage(imageUri) }
        coVerify {
            bikeRepository.addBike(
                uuid = any(),
                ownerId = "test-user-id",
                bikeName = bikeName,
                bikePrice = bikePrice.toDouble(),
                city = city,
                bikeImageUrl = mockUrl,
                startDate = any(),
                endDate = any()
            )
        }
    }
}
