package com.student.competishun.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.student.competishun.data.model.UpdateUserResponse
import com.student.competishun.data.repository.UpdateUserRepository
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateUserViewModel @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val updateUserRepository: UpdateUserRepository
) : ViewModel() {

    private val TAG: String = "UpdateUserViewModel"

    private val _updateUserResult = MutableLiveData<UpdateUserResponse?>()
    val updateUserResult: LiveData<UpdateUserResponse?> = _updateUserResult

    fun updateUser(updateUserInput: com.student.competishun.gatekeeper.type.UpdateUserInput) {
        val accessToken = sharedPreferencesManager.accessToken
        if (accessToken != null) {
            viewModelScope.launch {
                _updateUserResult.value = updateUserRepository.updateUser(updateUserInput, accessToken)
                sharedPreferencesManager.updateUserInput = updateUserInput
            }
        } else {
            Log.e(TAG, "Access token is null")
        }
    }

}
