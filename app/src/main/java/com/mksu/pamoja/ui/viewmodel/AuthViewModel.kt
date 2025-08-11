package com.mksu.pamoja.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mksu.pamoja.data.model.User
import com.mksu.pamoja.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for authentication operations
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    init {
        checkAuthState()
    }
    
    private fun checkAuthState() {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            loadUserData(firebaseUser.uid)
        }
    }
    
    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Email and password cannot be empty"
            )
            return
        }
        
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let { loadUserData(it.uid) }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = task.exception?.message ?: "Sign in failed"
                    )
                }
            }
    }
    
    fun signUp(email: String, password: String, firstName: String, lastName: String, studentId: String) {
        if (email.isBlank() || password.isBlank() || firstName.isBlank() || lastName.isBlank() || studentId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "All fields are required"
            )
            return
        }
        
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let { createUserProfile(it, firstName, lastName, studentId) }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = task.exception?.message ?: "Sign up failed"
                    )
                }
            }
    }
    
    private fun createUserProfile(firebaseUser: FirebaseUser, firstName: String, lastName: String, studentId: String) {
        val user = User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            firstName = firstName,
            lastName = lastName,
            studentId = studentId
        )
        
        viewModelScope.launch {
            val result = userRepository.createUserInFirestore(user)
            if (result.isSuccess) {
                _currentUser.value = user
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to create user profile"
                )
            }
        }
    }
    
    private fun loadUserData(userId: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUser()
                if (user != null) {
                    _currentUser.value = user
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true
                    )
                } else {
                    // Try to sync from Firestore
                    try {
                        userRepository.syncUserData(userId)
                        val syncedUser = userRepository.getUserById(userId)
                        if (syncedUser != null) {
                            _currentUser.value = syncedUser
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isAuthenticated = true
                            )
                        } else {
                            // User exists in Firebase Auth but not in Firestore
                            // This can happen if signup didn't complete properly
                            // Set authenticated state anyway to prevent crash
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isAuthenticated = true
                            )
                        }
                    } catch (e: Exception) {
                        // If sync fails, still set authenticated to prevent crash
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            error = "Failed to load user data, but login successful"
                        )
                    }
                }
            } catch (e: Exception) {
                // Prevent crash by setting authenticated state
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    error = "Login successful, but failed to load user profile"
                )
            }
        }
    }
    
    fun signOut() {
        auth.signOut()
        _currentUser.value = null
        _uiState.value = AuthUiState()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null
)
