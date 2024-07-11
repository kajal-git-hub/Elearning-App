package com.student.competishun.data.repository

import com.student.competishun.data.model.User

interface UserRepository {
    suspend fun getUsers(): List<User>
}
