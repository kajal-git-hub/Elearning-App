package xyz.penpencil.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.penpencil.competishun.data.repository.UserRepository
import com.student.competishun.gatekeeper.MyDetailsQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    private val _userDetails = MutableLiveData<Result<MyDetailsQuery.Data>>()
    val userDetails: LiveData<Result<MyDetailsQuery.Data>> = _userDetails

    fun fetchUserDetails() {
        viewModelScope.launch {
            val result = userRepository.getMyDetails()
            _userDetails.value = result
        }
    }

    private val _deleteAccountResult = MutableLiveData<Result<Boolean>>()
    val deleteAccountResult: LiveData<Result<Boolean>> get() = _deleteAccountResult


    fun deleteAccount() {
        viewModelScope.launch {
            val result = userRepository.deleteAccount()
            if (result.isSuccess) {
                _deleteAccountResult.postValue(Result.success(true))
            } else {
                _deleteAccountResult.postValue(Result.failure(result.exceptionOrNull() ?: Exception("Unknown error")))
            }
        }
    }
}
