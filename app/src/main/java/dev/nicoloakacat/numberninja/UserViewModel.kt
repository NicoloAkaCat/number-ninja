package dev.nicoloakacat.numberninja

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore

class UserViewModel: ViewModel() {
    private val _user = MutableLiveData<FirebaseUser?>(null)
    private val _isUserLogged = MutableLiveData<Boolean>(false)
    private val _displayName = MutableLiveData<String?>(null)
    private val _uid = MutableLiveData<String?>(null)
    private val _maxScore = MutableLiveData<Int>(0)
    private val _maxScoreText = MutableLiveData<String>("Max Score: ${_maxScore.value}")
    private val _nationality = MutableLiveData<Int>(0)

    val displayName: LiveData<String?> = _displayName
    val uid: LiveData<String?> = _uid
    val isUserLogged: LiveData<Boolean> = _isUserLogged
    val maxScore: LiveData<Int> = _maxScore
    val maxScoreText: LiveData<String> = _maxScoreText
    val nationality: LiveData<Int> = _nationality

    val setUser = {user: FirebaseUser? ->
        _user.value = user
        _displayName.value = user?.displayName
        _uid.value = user?.uid
        _isUserLogged.value = user != null
    }
    val setDataFromDB = {data: UserDB ->
        _maxScore.value = data.maxScore!!
        _maxScoreText.value = "Max Score: ${_maxScore.value}"
        _nationality.value = data.nationality!!
    }
    val setMaxScore = {digit: Int -> if(digit > _maxScore.value!!){
        _maxScore.value = digit
        _maxScoreText.value = "Max Score: ${_maxScore.value}"
        //TODO update db
    }}
    val updateMaxScoreDB = {maxScore: Int ->
        Firebase.firestore.collection("users").document(uid.value!!)
            .update("maxScore", maxScore)
            .addOnSuccessListener {

            }
            .addOnFailureListener{

            }
    }
}