package com.mksu.pamoja.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mksu.pamoja.data.model.Appointment
import com.mksu.pamoja.data.model.AppointmentStatus
import com.mksu.pamoja.data.model.Resource
import com.mksu.pamoja.data.model.User
import com.mksu.pamoja.data.repository.AppointmentRepository
import com.mksu.pamoja.data.repository.ResourceRepository
import com.mksu.pamoja.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository,
    private val resourceRepository: ResourceRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val currentUser = userRepository.getCurrentUser()
                if (currentUser != null) {
                    // For now, just load user info without complex data to prevent crashes
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = currentUser,
                        upcomingAppointments = emptyList(), // Simplified - no appointments for now
                        popularResources = emptyList(), // Simplified - no resources for now
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "User not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load home data"
                )
            }
        }
    }
    
    fun refreshData() {
        loadHomeData()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val upcomingAppointments: List<Appointment> = emptyList(),
    val popularResources: List<Resource> = emptyList(),
    val error: String? = null
)
