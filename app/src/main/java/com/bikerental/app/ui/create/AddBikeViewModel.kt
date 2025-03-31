package com.bikerental.app.ui.create

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import com.bikerental.app.data.repositories.AuthRepository
import com.bikerental.app.data.repositories.BikeRepository
import com.bikerental.app.ui.base.BaseViewModel
import com.bikerental.app.ui.navigation.Destination
import com.bikerental.app.ui.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlin.String

@HiltViewModel
class AddBikeViewModel @Inject constructor(
    navigator: Navigator,
    private val bikeRepository: BikeRepository,
    private val auth: AuthRepository
) : BaseViewModel(navigator) {

    companion object {
        const val TAG = "AddBikeViewModel"
    }

    private val _bikeName = MutableStateFlow("")
    private val _description = MutableStateFlow("")
    private val _city = MutableStateFlow("")
    private val _bikeNameError = MutableStateFlow("")
    private val _bikeDescriptionError = MutableStateFlow("")
    private val _bikeCityError = MutableStateFlow("")
    private val _firebaseError = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _bikeImageUri = MutableStateFlow<Uri?>(null)


    val bikeName = _bikeName.asStateFlow()
    val description = _description.asStateFlow()
    val city = _city.asStateFlow()
    val bikeNameError = _bikeNameError.asStateFlow()
    val bikeDescriptionError = _bikeDescriptionError.asStateFlow()
    val bikeCityError = _bikeCityError.asStateFlow()
    val firebaseError = _firebaseError.asStateFlow()
    val isLoading = _isLoading.asStateFlow()
    val bikeImageUri = _bikeImageUri.asStateFlow()

    fun onBikeImageChange(uri: Uri) {
        _bikeImageUri.value = uri
    }

    fun onBikeNameChange(input: String) {
        _bikeName.tryEmit(input)
        if (bikeNameError.value.isNotEmpty()) _bikeNameError.tryEmit("")
        _firebaseError.tryEmit("")
    }

    fun onBikeDescriptionChange(input: String) {
        _description.tryEmit(input)
        if (bikeDescriptionError.value.isNotEmpty()) _bikeDescriptionError.tryEmit("")
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
        if (description.value.length < 10) _bikeDescriptionError.tryEmit("Description length should be at least 10").run { error = true }
        if (city.value.length < 3) _bikeCityError.tryEmit("City length should be at least 3").run { error = true }
        return !error
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun addBike() {
        if(validate()) {
            _isLoading.tryEmit(true)
            _firebaseError.tryEmit("")

            launchFirebase {
                try {
//                    var imageUrl: String? = null
//                    // Upload image if selected
//                    _capturedImageUri.value?.let { uri ->
//                        imageUrl = bikeRepository.addBikeImage(uri).toString()
//                    }

                    bikeRepository.addBike(
                        UUID.randomUUID().toString(),
                        auth.getCurrentUser!!.uid,
                        bikeName.value, city.value,
//                        imageUrl.toString()
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