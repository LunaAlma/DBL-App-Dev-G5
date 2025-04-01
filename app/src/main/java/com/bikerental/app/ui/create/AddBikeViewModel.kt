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
import java.io.ByteArrayOutputStream
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
    private val _bikeNameError = MutableStateFlow("")
    private val _bikePriceError = MutableStateFlow("")
    private val _bikeCityError = MutableStateFlow("")
    private val _bikeImageError = MutableStateFlow("")
    private val _firebaseError = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _bikeImageUri = MutableStateFlow<Uri?>(null)

    val bikeName = _bikeName.asStateFlow()
    val bikePrice = _bikePrice.asStateFlow()
    val city = _city.asStateFlow()
    val bikeNameError = _bikeNameError.asStateFlow()
    val bikePriceError = _bikePriceError.asStateFlow()
    val bikeCityError = _bikeCityError.asStateFlow()
    val bikeImageError = _bikeImageError.asStateFlow()
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

    private fun validate(): Boolean {
        var error = false
        if (bikeName.value.length < 6) _bikeNameError.tryEmit("Bike Name length should be at least 6").run { error = true }
        if (!isPriceValid(bikePrice.value)) _bikePriceError.tryEmit("Not a valid price").run { error = true }
        if (city.value.length < 3) _bikeCityError.tryEmit("City length should be at least 3").run { error = true }
        if(bikeImageUri.value == null) _bikeImageError.tryEmit("Bike image is required").run { error = true }
        return !error
    }

    fun isPriceValid(finalPrice: String): Boolean {
        return finalPrice.isNotEmpty() &&
                finalPrice.toDoubleOrNull() != null &&
                finalPrice.matches(Regex("^\\d+\\.\\d{2}$"))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun addBike() {
        if(validate()) {
            _isLoading.tryEmit(true)
            _firebaseError.tryEmit("")

            launchFirebase {
                try {
                    val imageUri = _bikeImageUri.value ?: throw Exception("Profile image is required")
                    val uploadResult = bikeRepository.addBikeImage(imageUri)
                    val downloadUrl = uploadResult.getOrElse { throw Exception("Profile image upload failed") }

                    bikeRepository.addBike(
                        uuid = UUID.randomUUID().toString(),
                        ownerId = auth.getCurrentUser!!.uid,
                        bikeName = bikeName.value,
                        bikePrice = bikePrice.value.toDouble(),
                        city = city.value,
                        bikeImageUrl = downloadUrl
                        )
                } catch (e: Exception) {
                    _firebaseError.tryEmit(e.message ?: "Adding bike failed")
                } finally {
                    _isLoading.tryEmit(false)
                    navigator.navigateTo(Destination.Home.route, true)
                }
            }
        }
    }
}