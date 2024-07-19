package com.student.competishun.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.type.UpdateUserInput
import com.student.competishun.data.model.UpdateUserResponse
import com.student.competishun.data.repository.UpdateUserRepository
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateUserViewModel @Inject constructor(
    private val TAG: String = "UpdateUserViewModel",
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val updateUserRepository: UpdateUserRepository
):ViewModel() {

    private val _updateUserResult = MutableLiveData<UpdateUserResponse?>()
    val updateUserResult: LiveData<UpdateUserResponse?> = _updateUserResult

    fun updateUser(updateUserInput: UpdateUserInput){
        val accessToken = sharedPreferencesManager.accessToken
        if (accessToken != null )
        viewModelScope.launch {
            _updateUserResult.value = updateUserRepository.updateUser(updateUserInput, accessToken)
        } else {
            Log.e(TAG, "Access token is null")
        }
    }
}