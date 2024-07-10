package dev.nicoloakacat.numberninja.ui.rankings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.nicoloakacat.numberninja.db.UserData

class RankingsViewModel : ViewModel() {

    private val _users = MutableLiveData<List<UserData>>(listOf())

    val users: LiveData<List<UserData>> = _users

    fun setUsers(users: List<UserData>){
        _users.value = users
    }
}