package dev.nicoloakacat.numberninja

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import dev.nicoloakacat.numberninja.db.UserData

class UserViewModel: ViewModel() {
    private val _user = MutableLiveData<FirebaseUser?>(null)
    private val _isUserLogged = MutableLiveData<Boolean>(false)
    private val _displayName = MutableLiveData<String?>(null)
    private val _uid = MutableLiveData<String?>(null)
    private val _maxScore = MutableLiveData<Int>(0)
    private val _nationality = MutableLiveData<String>("Unknown")
    private val _nBetterPlayers = MutableLiveData<Long>(Long.MAX_VALUE)

    val displayName: LiveData<String?> = _displayName
    val uid: LiveData<String?> = _uid
    val isUserLogged: LiveData<Boolean> = _isUserLogged
    val maxScore: LiveData<Int> = _maxScore
    val nationality: LiveData<String> = _nationality
    val nBetterPlayers: LiveData<Long> = _nBetterPlayers

    val setUser = {user: FirebaseUser? ->
        _user.value = user
        _displayName.value = user?.displayName
        _uid.value = user?.uid
        _isUserLogged.value = user != null
    }
    val setDataFromDB = {data: UserData ->
        _maxScore.value = data.maxScore!!
        _nationality.value = data.nationality!!
        _nBetterPlayers.value = data.nBetterPlayers!!
    }
    val setMaxScore = { digit: Int ->
        _maxScore.value = digit
    }
    val setDisplayName = { newName: String ->
        _displayName.value = newName
    }
    val setNationality = { newNationality: String ->
        _nationality.value = newNationality.replace(" ", "_")
    }
    val setBetterPlayersCount = { n: Long ->
        _nBetterPlayers.value = n
    }
}