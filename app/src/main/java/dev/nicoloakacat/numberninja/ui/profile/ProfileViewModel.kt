package dev.nicoloakacat.numberninja.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class ProfileViewModel : ViewModel() {

    private val _user = MutableLiveData<FirebaseUser>(null)
    val user: LiveData<FirebaseUser> = _user

    fun setUser(user: FirebaseUser?) {
        this._user.value = user
    }

    private val _text = MutableLiveData<String>().apply {
        value = ""
    }
    val text: LiveData<String> = _text

    fun setText(text: String) {
        this._text.value = text
    }
}