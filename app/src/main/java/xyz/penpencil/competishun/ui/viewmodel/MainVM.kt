package xyz.penpencil.competishun.ui.viewmodel

import androidx.lifecycle.ViewModel
import xyz.penpencil.competishun.data.api.Repository
import xyz.penpencil.competishun.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainVM @Inject constructor(val repository: Repository) : ViewModel() {

    val userList = MutableSharedFlow<List<User>>()
    val loader = MutableSharedFlow<Boolean>()


    init {
        getUser()
    }

    fun getUser() {
        CoroutineScope(Dispatchers.IO).launch {
            loader.emit(true)
//            repository.makeApiCall(UserQuery()).collect {
//                when (it) {
//                    is ApiProcess.Success -> {
//                        loader.emit(false)
//                        val list = it.data?.pokemon_v2_pokemon?.map { it.toSimplePokemon() }
//                        userList.emit( list ?: emptyList())
//                    }
//
//                    is ApiProcess.Failure -> {
//
//                    }
//
//                    is ApiProcess.Loading -> {
//
//
//                    }
//                }
//            }
        }

    }

}