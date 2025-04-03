package com.bikerental.app.ui.create

import android.R.attr.description
import android.net.Uri
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.navigation.Navigator
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import kotlin.String

@HiltViewModel
class AddBikeViewModel @Inject constructor(
    navigator: Navigator,
    private val bikeRepository: BikeRepository,
    private val auth: AuthRepository,
    private val db: FirebaseStorage
) : BaseViewModel(navigator) {

    private val _bikeName = MutableStateFlow("")
    private val _bikePrice = MutableStateFlow("")
    private val _city = MutableStateFlow("")
    private val _selectedStartDate = MutableStateFlow<Timestamp?>(null)
    private val _selectedEndDate = MutableStateFlow<Timestamp?>(null)
    private val _bikeNameError = MutableStateFlow("")
    private val _bikePriceError = MutableStateFlow("")
    private val _bikeCityError = MutableStateFlow("")
    private val _bikeImageError = MutableStateFlow("")
    private val _selectedStartDateError = MutableStateFlow("")
    private val _selectedEndDateError = MutableStateFlow("")
    private val _firebaseError = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _bikeImageUri = MutableStateFlow<Uri?>(null)

    val bikeName = _bikeName.asStateFlow()
    val bikePrice = _bikePrice.asStateFlow()
    val city = _city.asStateFlow()
    val selectedStartDate = _selectedStartDate.asStateFlow()
    val selectedEndDate = _selectedEndDate.asStateFlow()
    val bikeNameError = _bikeNameError.asStateFlow()
    val bikePriceError = _bikePriceError.asStateFlow()
    val bikeCityError = _bikeCityError.asStateFlow()
    val bikeImageError = _bikeImageError.asStateFlow()
    val selectedStartDateError = _selectedStartDateError.asStateFlow()
    val selectedEndDateError = _selectedEndDateError.asStateFlow()
    val firebaseError = _firebaseError.asStateFlow()
    val isLoading = _isLoading.asStateFlow()
    val bikeImageUri = _bikeImageUri.asStateFlow()

    fun onBikeImageChange(uri: Uri) {
        _bikeImageUri.value = uri
        if (bikeImageUri.value != null) _bikeImageError.tryEmit("")
        _firebaseError.tryEmit("")
    }

    fun onBikeNameChange(input: String) {
        _bikeName.tryEmit(input)
        if (bikeNameError.value.isNotEmpty()) _bikeNameError.tryEmit("")
        _firebaseError.tryEmit("")
    }

    fun onBikePriceChange(input: String) {
        _bikePrice.tryEmit(input)
        if (bikePrice.value.isNotEmpty()) _bikePriceError.tryEmit("")
        _firebaseError.tryEmit("")
    }

    fun onBikeCityChange(input: String) {
        _city.tryEmit(input)
        if (bikeCityError.value.isNotEmpty()) _bikeCityError.tryEmit("")
        _firebaseError.tryEmit("")
    }
    fun onStartDateSelected(date: LocalDate) {
        println("DEBUG - Selected start date: $date")
        val instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        _selectedStartDate.value = Timestamp(instant.epochSecond, instant.nano)
    }

    fun onEndDateSelected(date: LocalDate) {
        println("DEBUG - Selected end date: $date")
        val instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        _selectedEndDate.value = Timestamp(instant.epochSecond, instant.nano)
    }

    private fun validate(): Boolean {
        var error = false
        if (bikeName.value.length < 6) _bikeNameError.tryEmit("Bike Name length should be at least 6").run { error = true }
        if (!isPriceValid(bikePrice.value)) _bikePriceError.tryEmit("Not a valid price").run { error = true }
        if (city.value.isEmpty()) _bikeCityError.tryEmit("Please select a city").run { error = true }
        if(bikeImageUri.value == null) _bikeImageError.tryEmit("Bike image is required").run { error = true }
        if (selectedStartDate.value == null) {
            _selectedStartDateError.tryEmit("Start date required")
            error = true
        }
        if (selectedEndDate.value == null) {
            _selectedEndDateError.tryEmit("End date required")
            error = true
        }
        if (selectedStartDate.value != null && selectedEndDate.value != null &&
            selectedStartDate.value!!.toDate().after(selectedEndDate.value!!.toDate())
        ) {
            _selectedStartDateError.tryEmit("Must be before end date")
            _selectedEndDateError.tryEmit("Must be after start date")
            error = true
        }

        return !error
    }

    fun isPriceValid(finalPrice: String): Boolean {
        return finalPrice.isNotEmpty() &&
                finalPrice.toDoubleOrNull() != null &&
                finalPrice.matches(Regex("^\\d+\\.\\d{2}$"))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun addBike() {
        println("DEBUG - Attempting to add bike...")
        println("DEBUG - Selected Dates: ${_selectedStartDate.value} to ${_selectedEndDate.value}")
        if(validate()) {
            println("DEBUG - Validation passed")
            _isLoading.tryEmit(true)
            _firebaseError.tryEmit("")

            val uuid = UUID.randomUUID().toString()

            launchFirebase {
                try {
                    val imageUri = _bikeImageUri.value ?: throw Exception("Profile image is required")
                    val uploadResult = bikeRepository.addBikeImage(imageUri)
                    val downloadUrl = uploadResult.getOrElse { throw Exception("Profile image upload failed") }

                    bikeRepository.addBike(
                        uuid = uuid,
                        ownerId = auth.getCurrentUser!!.uid,
                        bikeName = bikeName.value,
                        bikePrice = bikePrice.value.toDouble(),
                        city = city.value,
                        bikeImageUrl = downloadUrl,
                        startDate = _selectedStartDate.value!!,
                        endDate = _selectedEndDate.value!!
                        )
                } catch (e: Exception) {
                    _firebaseError.tryEmit(e.message ?: "Adding bike failed")
                } finally {
                    _isLoading.tryEmit(false)
                    goToMapAddBike(uuid)
                }
            }
        }
    }
    fun goToMapAddBike(uid: String) {
        navigator.navigateTo(Destination.Home.MapAddBike.route + uid)
    }
}