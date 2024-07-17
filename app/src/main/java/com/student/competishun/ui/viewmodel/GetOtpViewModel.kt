package com.student.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.data.repository.GetOtpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetOtpViewModel @Inject constructor(
    private val getOtpRepository: GetOtpRepository
):ViewModel() {
    private val _otpResult = MutableLiveData<Boolean?>()
    val otpResult: LiveData<Boolean?> = _otpResult

    fun getOtp(countryCode: String,mobileNum: String){
        viewModelScope.launch {
            _otpResult.value = getOtpRepository.getOtp(countryCode, mobileNum)
        }
    }

}