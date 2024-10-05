package xyz.penpencil.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.penpencil.competishun.data.model.UpdateUserResponse
import xyz.penpencil.competishun.data.repository.UpdateUserRepository
import com.student.competishun.gatekeeper.type.UpdateUserInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
            _updateUserResult.value =
                updateUserRepository.updateUser(
                    updateUserInput,
                    documentPhoto,
                    passportPhoto
                )
        }
    }
}