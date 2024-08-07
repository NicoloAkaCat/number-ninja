package dev.nicoloakacat.numberninja.ui.rankings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.nicoloakacat.numberninja.db.UserData

class RankingsViewModel : ViewModel() {
    private val _showError = MutableLiveData<Boolean>(false)
    private val _users = MutableLiveData<List<UserData>>(listOf())

    val users: LiveData<List<UserData>> = _users
    val showError: LiveData<Boolean> = _showError

    fun setUsers(users: List<UserData>){
        _users.value = users
    }
    fun setShowError(error: Boolean){
        _showError.value = error
    }
}