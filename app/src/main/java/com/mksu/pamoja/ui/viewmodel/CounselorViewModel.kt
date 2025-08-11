package com.mksu.pamoja.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mksu.pamoja.data.model.Counselor
import com.mksu.pamoja.data.repository.CounselorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Counselor-related operations
 */
@HiltViewModel
class CounselorViewModel @Inject constructor(
    private val counselorRepository: CounselorRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CounselorUiState())
    val uiState: StateFlow<CounselorUiState> = _uiState.asStateFlow()
    
    init {
        loadCounselors()
    }
    
    private fun loadCounselors() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // First try to sync from Firestore
                counselorRepository.syncCounselorsFromFirestore()
                
                // Then observe local data
                counselorRepository.getAllCounselorsOrderedByRating().collect { counselors ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        counselors = counselors,
                        filteredCounselors = counselors,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load counselors"
                )
            }
        }
    }
    
    fun searchCounselors(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    filteredCounselors = _uiState.value.counselors,
                    searchQuery = ""
                )
            } else {
                counselorRepository.searchCounselors(query).collect { results ->
                    _uiState.value = _uiState.value.copy(
                        filteredCounselors = results,
                        searchQuery = query
                    )
                }
            }
        }
    }
    
    fun filterBySpecialization(specialization: String) {
        viewModelScope.launch {
            if (specialization.isBlank() || specialization == "All") {
                _uiState.value = _uiState.value.copy(
                    filteredCounselors = _uiState.value.counselors,
                    selectedSpecialization = "All"
                )
            } else {
                counselorRepository.getCounselorsBySpecialization(specialization).collect { results ->
                    _uiState.value = _uiState.value.copy(
                        filteredCounselors = results,
                        selectedSpecialization = specialization
                    )
                }
            }
        }
    }
    
    fun showAvailableOnly(showAvailableOnly: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(showAvailableOnly = showAvailableOnly)
            
            if (showAvailableOnly) {
                counselorRepository.getAvailableCounselors().collect { results ->
                    _uiState.value = _uiState.value.copy(filteredCounselors = results)
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    filteredCounselors = _uiState.value.counselors
                )
            }
        }
    }
    
    suspend fun getCounselorById(counselorId: String): Counselor? {
        return counselorRepository.getCounselorById(counselorId)
    }
    
    fun refreshCounselors() {
        loadCounselors()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class CounselorUiState(
    val isLoading: Boolean = false,
    val counselors: List<Counselor> = emptyList(),
    val filteredCounselors: List<Counselor> = emptyList(),
    val searchQuery: String = "",
    val selectedSpecialization: String = "All",
    val showAvailableOnly: Boolean = false,
    val error: String? = null
)
