package com.student.competishun.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.data.model.VerifyOtpResponse
import com.student.competishun.data.repository.VerifyOtpRepository
import com.student.competishun.gatekeeper.type.VerifyOtpInput
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val verifyOtpRepository: VerifyOtpRepository
) : ViewModel() {

    private val _verifyOtpResult = MutableLiveData<VerifyOtpResponse?>()
    val verifyOtpResult: LiveData<VerifyOtpResponse?> = _verifyOtpResult

    fun verifyOtp(countryCode: String, mobileNumber: String, otp: Int){
        viewModelScope.launch {
            val verifyOtpInput = VerifyOtpInput(countryCode, mobileNumber, otp)
             val response = verifyOtpRepository.verifyOtp(verifyOtpInput)
            _verifyOtpResult.value = response
            response?.accessToken?.let {
                Log.e("Bearer Access token", response.accessToken.toString())
                sharedPreferencesManager.accessToken = it
            }

        }
    }
}
