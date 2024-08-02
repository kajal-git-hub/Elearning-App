package com.student.competishun.data.model

data class InstallmentModel(
    val title: String,
    val amount: String,
    val nestedInstallmentList: List<InstallmentItemModel>
)

data class  InstallmentItemModel(
    val description: String,
)
