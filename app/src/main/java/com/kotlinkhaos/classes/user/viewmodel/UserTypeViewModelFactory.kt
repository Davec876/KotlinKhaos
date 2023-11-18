package com.kotlinkhaos.classes.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UserViewModelFactory(private val userTypeDataStore: UserTypeStore) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userTypeDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
