package com.student.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.type.UpdateUserInput
import com.student.competishun.data.model.UpdateUserResponse
import com.student.competishun.data.repository.UpdateUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateUserViewModel @Inject constructor(
    private val updateUserRepository: UpdateUserRepository
):ViewModel() {

    private val _updateUserResult = MutableLiveData<UpdateUserResponse?>()
    val updateUserResult: LiveData<UpdateUserResponse?> = _updateUserResult

    fun updateUser(updateUserInput: UpdateUserInput){
        viewModelScope.launch {
            _updateUserResult.value = updateUserRepository.updateUser(updateUserInput)
        }
    }
}