package dev.nicoloakacat.numberninja.ui.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayViewModel : ViewModel() {

    private var countdown: Thread? = null
    private val _countdownProgress = MutableLiveData<Int>().apply {
        value = 5
    }
    // used String type due to the possibility of players guessing a number so big it doesn't fit into Int
    private val _numberToGuess = MutableLiveData<String>().apply {
        value= "0"
    }
    private val _resultMessage = MutableLiveData<String>().apply {
        value= ""
    }
    
    val number: LiveData<String> = _numberToGuess
    val countdownProgress: LiveData<Int> = _countdownProgress
    val resultMessage: LiveData<String> = _resultMessage

    val setNumberToGuess = {number: String ->  _numberToGuess.value = number }
    val startCountdown = {
        _countdownProgress.value = 5
        countdown = Thread{
            for(i in 4 downTo 0){
                try {
                    Thread.sleep(1000)
                }catch (e: Exception){
                    // interrupted by onDestroyView i.e. when changing page
                    return@Thread
                }
                _countdownProgress.postValue(i)
            }
        }
        countdown!!.start()
    }
    val stopCountdown = { countdown?.interrupt(); _countdownProgress.value = 5 }

}