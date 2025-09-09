package com.example.catalist.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserAccountStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val store = context.userAccountDataStore

    private object Keys {
        val FULL_NAME = stringPreferencesKey("full_name")
        val NICKNAME = stringPreferencesKey("nickname")
        val EMAIL = stringPreferencesKey("email")
    }

    val fullName: Flow<String?> = store.data.map { it[Keys.FULL_NAME] }
    val nickname: Flow<String?> = store.data.map { it[Keys.NICKNAME] }
    val email: Flow<String?> = store.data.map { it[Keys.EMAIL] }

    val isLoggedIn: Flow<Boolean> = combine(
        fullName,
        nickname,
        email
    ) { name, nick, mail ->
        !name.isNullOrBlank() && !nick.isNullOrBlank() && !mail.isNullOrBlank()
    }

    suspend fun createAccount(fullName: String, nickname: String, email: String) {
        store.edit { preferences ->
            preferences[Keys.FULL_NAME] = fullName
            preferences[Keys.NICKNAME] = nickname
            preferences[Keys.EMAIL] = email
        }
    }

    suspend fun clearAccount() {
        store.edit { it.clear() }
    }
}