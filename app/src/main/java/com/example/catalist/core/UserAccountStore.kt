package com.example.catalist.core.data

import android.content.Context

import androidx.datastore.preferences.preferencesDataStore

internal val Context.userAccountDataStore by preferencesDataStore("user_account")

