package dev.nicoloakacat.numberninja.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.nicoloakacat.numberninja.db.UserData

class ProfileViewModel : ViewModel() {

    private val _showError = MutableLiveData<Boolean>(false)

    val showError: LiveData<Boolean> = _showError

    fun setShowError(error: Boolean){
        _showError.value = error
    }
}