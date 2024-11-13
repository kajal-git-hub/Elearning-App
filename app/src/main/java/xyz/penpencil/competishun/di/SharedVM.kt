package xyz.penpencil.competishun.di

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Stack

class SharedVM : ViewModel() {
    private val _watchedDuration = MutableLiveData<Int>()
    val watchedDuration: LiveData<Int> get() = _watchedDuration

    fun setWatchedDuration(duration: Int) {
        _watchedDuration.value = duration
    }
    private val folderInfoStack: Stack<FolderInfo> = Stack()

    fun addFolderInfo(subFolders: String, folderName: String, folderId: String, folderCount: String) {
        val newInfo = FolderInfo(subFolders, folderName, folderId, folderCount)
        folderInfoStack.push(newInfo)
    }

    fun popFolderInfo(): FolderInfo? {
        return if (folderInfoStack.isNotEmpty()) {
            folderInfoStack.pop()
        } else {
            null
        }
    }

    fun clearFolderInfoStack() {
        folderInfoStack.clear()
    }

    fun getFolderInfoStackSize(): Int {
        return folderInfoStack.size
    }
}

data class FolderInfo(
    val subFolders: String,
    val folderName: String,
    val folderId: String,
    val folderCount: String
)
