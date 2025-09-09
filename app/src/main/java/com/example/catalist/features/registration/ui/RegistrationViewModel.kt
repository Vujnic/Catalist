package com.example.catalist.features.registration.ui

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catalist.core.data.UserAccountStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userAccountStore: UserAccountStore
) : ViewModel() {

    data class RegistrationState(
        val fullName: String = "",
        val nickname: String = "",
        val email: String = "",
        val fullNameError: String? = null,
        val nicknameError: String? = null,
        val emailError: String? = null,
        val isLoading: Boolean = false,
        val isRegistrationComplete: Boolean = false
    )

    private val _state = MutableStateFlow(RegistrationState())
    val state = _state.asStateFlow()

    fun onFullNameChanged(value: String) {
        _state.update { it.copy(
            fullName = value,
            fullNameError = validateFullName(value)
        )}
    }

    fun onNicknameChanged(value: String) {
        _state.update { it.copy(
            nickname = value,
            nicknameError = validateNickname(value)
        )}
    }

    fun onEmailChanged(value: String) {
        _state.update { it.copy(
            email = value,
            emailError = validateEmail(value)
        )}
    }

    private fun validateFullName(value: String): String? =
        if (value.isBlank()) "Full name is required" else null

    private fun validateNickname(value: String): String? = when {
        value.isBlank() -> "Nickname is required"
        !value.matches(Regex("^[a-zA-Z0-9_]+$")) -> "Only letters, numbers and underscore allowed"
        else -> null
    }

    private fun validateEmail(value: String): String? = when {
        value.isBlank() -> "Email is required"
        !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Invalid email format"
        else -> null
    }

    fun onRegister() {
        val currentState = _state.value

        val fullNameError = validateFullName(currentState.fullName)
        val nicknameError = validateNickname(currentState.nickname)
        val emailError = validateEmail(currentState.email)

        if (fullNameError != null || nicknameError != null || emailError != null) {
            _state.update { it.copy(
                fullNameError = fullNameError,
                nicknameError = nicknameError,
                emailError = emailError
            )}
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                userAccountStore.createAccount(
                    fullName = currentState.fullName,
                    nickname = currentState.nickname,
                    email = currentState.email
                )
                _state.update { it.copy(isRegistrationComplete = true) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}