package com.student.competishun.data.model

data class VerifyOtpResponse(
    val user: User?,
    val refreshToken: String?,
    val accessToken: String?
)
