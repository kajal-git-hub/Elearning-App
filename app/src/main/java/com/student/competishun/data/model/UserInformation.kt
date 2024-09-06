package com.student.competishun.data.model


data class UserInformation(
    public val targetYear: Int?,
    public val reference: String?,
    public val preparingFor: String?,
    public val id: String,
    public val waCountryCode: String?,
    public val documentPhoto: String?,
    public val schoolName: String?,
    public val tShirtSize: String?,
    public val address: Address?,
)

public data class Address(
    public val city: String?,
)

