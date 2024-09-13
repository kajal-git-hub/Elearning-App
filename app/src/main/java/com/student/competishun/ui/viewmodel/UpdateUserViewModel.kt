package com.student.competishun.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.data.model.UpdateUserResponse
import com.student.competishun.data.repository.UpdateUserRepository
import com.student.competishun.gatekeeper.type.UpdateUserInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UpdateUserViewModel @Inject constructor(
    private val updateUserRepository: UpdateUserRepository
) : ViewModel() {

    private val _updateUserResult = MutableLiveData<UpdateUserResponse?>()
    val updateUserResult: LiveData<UpdateUserResponse?> = _updateUserResult

    fun updateUser(
        updateUserInput: UpdateUserInput,
        documentPhoto: String?,
        passportPhoto: String?
    ) {
        viewModelScope.launch {
            _updateUserResult.value = documentPhoto?.let {
                passportPhoto?.let { it1 ->
                    updateUserRepository.updateUser(
                        updateUserInput,
                        it,
                        it1
                    )
                }
            }
        }
    }
}
