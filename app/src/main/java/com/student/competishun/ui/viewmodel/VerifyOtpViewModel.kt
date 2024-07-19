package com.student.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.data.model.VerifyOtpResponse
import com.student.competishun.data.repository.VerifyOtpRepository
import com.student.competishun.type.VerifyOtpInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    private val verifyOtpRepository: VerifyOtpRepository
) : ViewModel() {

    private val _verifyOtpResult = MutableLiveData<VerifyOtpResponse?>()
    val verifyOtpResult: LiveData<VerifyOtpResponse?> = _verifyOtpResult

    fun verifyOtp(countryCode: String, mobileNumber: String, otp: Int) {
        viewModelScope.launch {
            val verifyOtpInput = VerifyOtpInput(
                countryCode = countryCode,
                mobileNumber = mobileNumber,
                otp = otp
            )
            _verifyOtpResult.value = verifyOtpRepository.verifyOtp(verifyOtpInput)
        }
    }
}
