package xyz.penpencil.competishun.data.model

data class User(
    val id: String,
    val mobileNumber: String,
    val fullName: String?,
    val countryCode: String,
    val email:String,
    val userInformation: UserInformation
)