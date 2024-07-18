package com.student.competishun.data.model

data class VerifyOtpInput(
    val countryCode: String,
    val mobileNumber: String,
    val otp: Int
)
