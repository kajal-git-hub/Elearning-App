package xyz.penpencil.competishun.di

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedVM : ViewModel() {
    private val _watchedDuration = MutableLiveData<Int>()
    val watchedDuration: LiveData<Int> get() = _watchedDuration

    fun setWatchedDuration(duration: Int) {
        _watchedDuration.value = duration
    }
}
