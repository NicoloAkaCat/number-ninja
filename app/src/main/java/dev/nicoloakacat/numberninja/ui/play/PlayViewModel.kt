package dev.nicoloakacat.numberninja.ui.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayViewModel : ViewModel() {

    val setNumberToGuess = {number: String ->  _numberToGuess.value = number }
    val startCountdown = {
        _progress.value = 5
        Thread{
            for(i in 4 downTo 0){
                Thread.sleep(1000)
                _progress.postValue(i)
            }
        }.start()
    }

    private val _progress = MutableLiveData<Int>().apply {
        value = 5
    }

    private val _maxScore = MutableLiveData<String>().apply {
        value = "Max Score: 0" //TODO change to get maxScore from firebase, probably UserViewModel
    }

    // used String type due to the possibility of players guessing a number so big it doesn't fit into Int
    private val _numberToGuess = MutableLiveData<String>().apply {
        value= "0"
    }


    val maxScore: LiveData<String> = _maxScore
    val number: LiveData<String> = _numberToGuess
    val progress: LiveData<Int> = _progress
}