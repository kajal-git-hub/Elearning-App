package com.student.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.data.repository.UserRepository
import com.student.competishun.gatekeeper.MyDetailsQuery
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _userDetails = MutableLiveData<Result<MyDetailsQuery.Data>>()
    val userDetails: LiveData<Result<MyDetailsQuery.Data>> = _userDetails

    fun fetchUserDetails() {
        viewModelScope.launch {
            val result = userRepository.getMyDetails()
            _userDetails.value = result
        }
    }
}
