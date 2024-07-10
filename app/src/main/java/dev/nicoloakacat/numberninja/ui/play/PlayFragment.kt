package dev.nicoloakacat.numberninja.ui.play

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dev.nicoloakacat.numberninja.db.UserStorage
import dev.nicoloakacat.numberninja.UserViewModel
import dev.nicoloakacat.numberninja.databinding.FragmentPlayBinding
import dev.nicoloakacat.numberninja.hide
import dev.nicoloakacat.numberninja.show
import dev.nicoloakacat.numberninja.showResultMessage
import kotlinx.coroutines.launch


class PlayFragment : Fragment() {

    private lateinit var binding: FragmentPlayBinding
    private val viewModel: PlayViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private var currentDigit: Int = 1

    private var playerHasNewMaxScore: Boolean = false;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.userViewModel = userViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerHasNewMaxScore = false
        currentDigit = 1

        binding.playBtn.setOnClickListener {
            viewModel.setNumberToGuess(getRandomNumber(currentDigit))
            hide(binding.introGroup)
            show(binding.showNumberGroup)
            viewModel.startCountdown()
        }
        viewModel.countdownProgress.observe(viewLifecycleOwner) {
            if(it == 0) {
                hide(binding.showNumberGroup)
                show(binding.guessNumberGroup)
            }
        }
        binding.playGuessBtn.setOnClickListener {
            val guess = binding.playGuessNumber.text.toString()
            binding.playGuessNumber.text?.clear()
            // close keyboard
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            // check guess
            if(viewModel.number.value == guess){
                showResultMessage(binding.playResultMessageSuccess, requireContext())
                if(currentDigit > userViewModel.maxScore.value!!) {
                    userViewModel.setMaxScore(currentDigit)
                    playerHasNewMaxScore = true
                }
                currentDigit += 1
                viewModel.setNumberToGuess(getRandomNumber(currentDigit))
                hide(binding.guessNumberGroup)
                show(binding.showNumberGroup)
                viewModel.startCountdown()
            }else{
                showResultMessage(binding.playResultMessageError, requireContext())
                lifecycleScope.launch { updateMaxScore() }
                currentDigit = 1
                hide(binding.guessNumberGroup)
                show(binding.introGroup)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // if the user change page before the countdown has ended we need to stop it
        viewModel.stopCountdown()

        lifecycleScope.launch { updateMaxScore() }
    }

    private suspend fun updateMaxScore() {
        if(userViewModel.isUserLogged.value!! && playerHasNewMaxScore) {
            try{
                UserStorage.updateScore(userViewModel.maxScore.value!!, userViewModel.uid.value!!)
                val count = UserStorage.countBetterPlayersThan(userViewModel.maxScore.value!!)

                if(count != userViewModel.nBetterPlayers.value) {
                    UserStorage.updateBetterPlayersCount(count, userViewModel.uid.value!!)
                }
            }catch (e: Exception){
                Log.e("UPDATE_MAX_SCORE", "An error occurred when updating max score")
            }
        }
    }

    private fun getRandomNumber(digits: Int): String {
        var n = ""
        repeat(digits){
            n += if(it == 0)
                (1..9).random().toString()
            else
                (0..9).random().toString()
        }
        return n
    }

}