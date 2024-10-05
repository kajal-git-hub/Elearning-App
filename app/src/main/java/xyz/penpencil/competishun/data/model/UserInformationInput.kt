package xyz.penpencil.competishun.data.model

data class UserInformationInput(
    val id: String,
    val preparingFor: String?,
    val targetYear: Int?,
    val city: String?,
    val reference: String?
)
