package com.student.competishun.ui.viewmodel

import com.student.competishun.data.repository.UpdateUserRepository
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.student.competishun.data.model.UpdateUserResponse
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.gatekeeper.type.UpdateUserInput

@HiltViewModel
class UpdateUserViewModel @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val updateUserRepository: UpdateUserRepository
) : ViewModel() {

    private val _updateUserResult = MutableLiveData<UpdateUserResponse?>()
    val updateUserResult: LiveData<UpdateUserResponse?> = _updateUserResult

    fun updateUser() {
        val accessToken = sharedPreferencesManager.accessToken ?: return

        val updateUserInput = UpdateUserInput(
            city = Optional.present("noida"),
            fullName = Optional.present("kajal"),
            preparingFor = Optional.present("Board"),
            reference = Optional.present("Advertisement"),
            targetYear = Optional.present(2025)
        )

        viewModelScope.launch {
            try {
                val result = updateUserRepository.updateUser(updateUserInput, accessToken)
                _updateUserResult.value = result
                Log.d("UpdateUserViewModel", "Update successful: $result")
            } catch (e: ApolloException) {
                Log.e("UpdateUserViewModel", "Update failed: ${e.message}")
                _updateUserResult.value = null
            }
        }
    }
}
