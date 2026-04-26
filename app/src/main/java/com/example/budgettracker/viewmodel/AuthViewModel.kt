package com.example.budgettracker.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class User(
    val username: String,
    val email: String
)

data class AuthState(
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Simple in-memory user storage for demonstration
    private val registeredUsers = mutableMapOf(
        "admin" to Pair("admin@example.com", "1234")
    )

    fun login(username: String, password: String): Boolean {
        return if (username.isBlank() || password.isBlank()) {
            _authState.update { it.copy(errorMessage = "Username and password cannot be empty") }
            false
        } else if (registeredUsers.containsKey(username)) {
            val (email, storedPassword) = registeredUsers[username]!!
            if (storedPassword == password) {
                _authState.update {
                    it.copy(
                        isLoggedIn = true,
                        currentUser = User(username, email),
                        errorMessage = null
                    )
                }
                true
            } else {
                _authState.update { it.copy(errorMessage = "Invalid password") }
                false
            }
        } else {
            _authState.update { it.copy(errorMessage = "User not found") }
            false
        }
    }

    fun register(username: String, email: String, password: String): Boolean {
        return if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.update { it.copy(errorMessage = "All fields are required") }
            false
        } else if (registeredUsers.containsKey(username)) {
            _authState.update { it.copy(errorMessage = "Username already exists") }
            false
        } else {
            registeredUsers[username] = Pair(email, password)
            _authState.update {
                it.copy(
                    isLoggedIn = true,
                    currentUser = User(username, email),
                    errorMessage = null
                )
            }
            true
        }
    }

    fun logout() {
        _authState.update {
            it.copy(
                isLoggedIn = false,
                currentUser = null,
                errorMessage = null
            )
        }
    }

    fun clearError() {
        _authState.update { it.copy(errorMessage = null) }
    }
}

