package com.kotlinkhaos.classes.user.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kotlinkhaos.classes.user.UserType
import kotlinx.coroutines.flow.first

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_type_preferences")

class UserTypeStore(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val USER_TYPE_KEY = stringPreferencesKey("user_type")
    }

    suspend fun saveUserType(userType: UserType) {
        dataStore.edit { preferences ->
            preferences[USER_TYPE_KEY] = userType.name
        }
    }

    suspend fun loadUserType(): UserType? {
        val preferences = dataStore.data.first()
        val userTypeString = preferences[USER_TYPE_KEY] ?: return null
        return try {
            UserType.valueOf(userTypeString)
        } catch (e: IllegalArgumentException) {
            UserType.NONE
        }
    }

    suspend fun clearUserType() {
        dataStore.edit { preferences ->
            preferences.remove(USER_TYPE_KEY)
        }
    }
}
