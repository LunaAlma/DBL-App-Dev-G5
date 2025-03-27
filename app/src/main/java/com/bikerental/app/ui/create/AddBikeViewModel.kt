package com.bikerental.app.ui.create

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikerental.app.data.model.BikeRental
import com.bikerental.app.data.repository.BikeRentalRepository
import com.bikerental.app.data.service.AuthService
import com.bikerental.app.data.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBikeViewModel @Inject constructor(
    private val bikeRentalRepository: BikeRentalRepository,
    private val storageService: StorageService,
    private val authService: AuthService
) : ViewModel() {

    companion object {
        const val TAG = "AddBikeViewModel"
    }

    private val _uiState = MutableStateFlow<AddBikeUiState>(AddBikeUiState.Initial)
    val uiState: StateFlow<AddBikeUiState> = _uiState

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    fun setSelectedImage(uri: Uri) {
        _selectedImageUri.value = uri
    }

    fun addBikeRental(
        bikeType: String,
        description: String,
        city: String,
        rentalStartDate: String,
        rentalEndDate: String
    ) {
        viewModelScope.launch {
            _uiState.value = AddBikeUiState.Loading

            try {
                // Get current user ID
                val userId = authService.currentUserId
                    ?: throw Exception("User not authenticated")

                // Upload image if selected
                val imageUrl = _selectedImageUri.value?.let { uri ->
                    storageService.uploadBikeImage(uri)
                        .getOrThrow()
                } ?: ""

                val bikeRental = BikeRental(
                    bikeType = bikeType,
                    description = description,
                    city = city,
                    rentalStartDate = rentalStartDate,
                    rentalEndDate = rentalEndDate,
                    imageUrl = imageUrl,
                    userId = userId
                )

                bikeRentalRepository.addBikeRental(bikeRental)
                    .onSuccess { id ->
                        _uiState.value = AddBikeUiState.Success(id)
                    }
                    .onFailure { error ->
                        _uiState.value = AddBikeUiState.Error(error.message ?: "Unknown error occurred")
                    }
            } catch (e: Exception) {
                _uiState.value = AddBikeUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun resetState() {
        _uiState.value = AddBikeUiState.Initial
        _selectedImageUri.value = null
    }
}

sealed class AddBikeUiState {
    object Initial : AddBikeUiState()
    object Loading : AddBikeUiState()
    data class Success(val bikeId: String) : AddBikeUiState()
    data class Error(val message: String) : AddBikeUiState()
}