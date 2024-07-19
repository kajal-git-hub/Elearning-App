package com.student.competishun.data.model

import com.apollographql.apollo3.api.Optional

data class UpdateUserInput(
    val city: Optional<String?> = Optional.Absent,
    val fullName: Optional<String?> = Optional.Absent,
    val preparingFor: Optional<String?> = Optional.Absent,
    val reference: Optional<String?> = Optional.Absent,
    val targetYear: Optional<Int?> = Optional.Absent
)