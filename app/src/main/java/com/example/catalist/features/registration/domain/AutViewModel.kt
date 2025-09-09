package com.example.catalist.features.registration.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catalist.core.data.UserAccountStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userAccountStore: UserAccountStore
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _event = MutableSharedFlow<AuthEvent>()

    init {
        observeEvents()
        sendEvent(AuthEvent.CheckAuth)
    }

    fun sendEvent(event: AuthEvent) {
        viewModelScope.launch { _event.emit(event) }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            _event.collect { event ->
                when (event) {
                    AuthEvent.CheckAuth -> checkIfLoggedIn()
                }
            }
        }
    }

    private fun checkIfLoggedIn() {
        viewModelScope.launch {
            userAccountStore.isLoggedIn
                .take(1)
                .collect { loggedIn ->
                    _state.value = if (loggedIn) AuthState.LoggedIn else AuthState.NotLoggedIn
                }
        }
    }
}
