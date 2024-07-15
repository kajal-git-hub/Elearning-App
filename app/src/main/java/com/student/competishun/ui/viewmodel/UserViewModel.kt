//package com.student.competishun.ui.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.student.competishun.data.model.User
//import com.student.competishun.data.repository.UserRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class UserViewModel @Inject constructor(
//    private val userRepository: UserRepository
//) : ViewModel() {
//
//    val users = mutableListOf<User>()
//
//    fun fetchUsers() {
//        viewModelScope.launch {
//            try {
//                users.addAll(userRepository.getUsers())
//            } catch (e: Exception) {
//                // Handle error
//            }
//        }
//    }
//}