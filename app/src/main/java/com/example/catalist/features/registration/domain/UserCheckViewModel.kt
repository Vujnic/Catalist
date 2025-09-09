package com.example.catalist.features.registration.domain

import androidx.lifecycle.ViewModel
import com.example.catalist.core.data.UserAccountStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class UserCheckViewModel @Inject constructor(
    private val userAccountStore: UserAccountStore
) : ViewModel() {
    val isLoggedIn: Flow<Boolean> = userAccountStore.isLoggedIn
}