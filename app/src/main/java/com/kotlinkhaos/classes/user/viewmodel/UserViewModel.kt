package com.kotlinkhaos.classes.user.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlinkhaos.classes.user.User
import kotlinx.coroutines.launch

class UserViewModel(private val userTypeDataStore: UserStore) : ViewModel() {
    private var _storedUserDetails = MutableLiveData<StoredUserDetails?>()
    val storedUserDetails: LiveData<StoredUserDetails?> = _storedUserDetails

    private var _userDetails = MutableLiveData<StoredUserDetails?>()
    val userDetails: LiveData<StoredUserDetails?> = _userDetails

    fun loadDetails() {
        viewModelScope.launch {
            val user = User.getUser()
            if (user == null) {
                _userDetails.value = null
                _storedUserDetails.value = null;
                userTypeDataStore.clearUserDetails()
                return@launch
            }
            val userDetails = StoredUserDetails(user.getCourseId(), user.getType())
            _userDetails.value = userDetails
            userTypeDataStore.saveUserDetails(userDetails)
        }
    }

    fun loadDetailsFromStore() {
        viewModelScope.launch {
            val storedUserDetails = userTypeDataStore.loadUserDetails()
            _storedUserDetails.value = storedUserDetails
        }
    }

    fun logout() {
        viewModelScope.launch {
            User.logout()
            userTypeDataStore.clearUserDetails()
            _storedUserDetails.value = null;
            _userDetails.value = null;
        }
    }
}
