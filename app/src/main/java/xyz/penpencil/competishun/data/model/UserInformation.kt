package xyz.penpencil.competishun.data.model

import com.student.competishun.gatekeeper.type.DateTime


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
    public val gender:String?,
    public val dob:Any?
)

public data class Address(
    public val city: String?,
)

