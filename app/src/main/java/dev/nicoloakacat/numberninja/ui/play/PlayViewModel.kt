package dev.nicoloakacat.numberninja.ui.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayViewModel : ViewModel() {

    val maxTime = 5
    private var countdown: Thread? = null
    private val _countdownProgress = MutableLiveData<Int>(maxTime)
    // used String type due to the possibility of players guessing a number so big it doesn't fit into Int
    private val _numberToGuess = MutableLiveData<String>("0")
    
    val number: LiveData<String> = _numberToGuess
    val countdownProgress: LiveData<Int> = _countdownProgress

    val setNumberToGuess = {number: String ->  _numberToGuess.value = number }
    val startCountdown = {
        _countdownProgress.value = maxTime
        countdown = Thread{
            for(i in maxTime-1 downTo 0){
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
    val stopCountdown = { countdown?.interrupt(); _countdownProgress.value = maxTime }

}