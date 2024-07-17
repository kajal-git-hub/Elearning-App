package com.student.competishun.data.api

sealed class ApiProcess<out T>() {
    class Success< T>(val data: T) : ApiProcess<T>()
    class Failure<T>(val message: String, val data: T? = null, val errorCode:Int?=null) : ApiProcess<T>()
    object Loading : ApiProcess<Nothing>()
}