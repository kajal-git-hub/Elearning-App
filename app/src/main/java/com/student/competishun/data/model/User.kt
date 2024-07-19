package com.student.competishun.data.model

data class User(
    val id: String,
    val mobileNumber: String,
    val fullName: String?,
    val countryCode: String,
    val userInformation: UserInformation
)