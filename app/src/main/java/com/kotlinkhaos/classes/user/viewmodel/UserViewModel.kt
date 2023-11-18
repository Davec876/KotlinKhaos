package com.kotlinkhaos.classes.user.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlinkhaos.classes.user.User
import com.kotlinkhaos.classes.user.UserType
import kotlinx.coroutines.launch

class UserViewModel(private val userTypeDataStore: UserTypeStore) : ViewModel() {
    private var _storedUserType = MutableLiveData<UserType?>()
    val storedUserType: LiveData<UserType?> = _storedUserType

    private var _userType = MutableLiveData<UserType?>()
    val userType: LiveData<UserType?> = _userType

    fun loadType() {
        viewModelScope.launch {
            val user = User.getUser()
            _userType.value = user?.getType()
            if (user != null) {
                userTypeDataStore.saveUserType(user.getType())
            }
        }
    }

    fun loadTypeFromStore() {
        viewModelScope.launch {
            _storedUserType.value = userTypeDataStore.loadUserType()
        }
    }

    fun logout() {
        viewModelScope.launch {
            User.logout()
            userTypeDataStore.clearUserType()
            _storedUserType.value = null;
            _userType.value = null;
        }
    }
}
