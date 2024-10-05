package xyz.penpencil.competishun.data.model

import xyz.penpencil.competishun.data.model.User

data class VerifyOtpResponse(
    val user: User?,
    val refreshToken: String?,
    val accessToken: String?
)

data class GoogleResponse(
    val user: User?,
    val refreshToken: String?,
    val accessToken: String?
)
