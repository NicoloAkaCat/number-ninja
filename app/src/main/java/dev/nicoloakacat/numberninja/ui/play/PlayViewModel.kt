package dev.nicoloakacat.numberninja.ui.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayViewModel : ViewModel() {

    private var countdown: Thread? = null

    val setNumberToGuess = {number: String ->  _numberToGuess.value = number }
    val startCountdown = {
        _progress.value = 5
        countdown = Thread{
            for(i in 4 downTo 0){
                try {
                    Thread.sleep(1000)
                }catch (e: Exception){
                    // interrupted by onDestroyView i.e. when changing page
                    return@Thread
                }
                _progress.postValue(i)
            }
        }
        countdown!!.start()
    }
    val stopCountdown = { countdown?.interrupt() }

    private val _progress = MutableLiveData<Int>().apply {
        value = 100
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