package com.student.competishun.ui.main

import com.student.competishun.UserQuery
import com.student.competishun.data.model.User


fun UserQuery.Pokemon_v2_pokemon.toSimplePokemon(): User {
    return User(
        name = name,
        id = id.toString(),
        email = name,
    )
}
