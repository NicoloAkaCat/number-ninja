package dev.nicoloakacat.numberninja.ui.play

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import dev.nicoloakacat.numberninja.R
import dev.nicoloakacat.numberninja.UserStorage
import dev.nicoloakacat.numberninja.UserViewModel
import dev.nicoloakacat.numberninja.databinding.FragmentPlayBinding


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

        this.playerHasNewMaxScore = false

        super.onViewCreated(view, savedInstanceState)
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
                showResultMessage(binding.playResultMessageSuccess)
                if(currentDigit > userViewModel.maxScore.value!!) {
                    userViewModel.setMaxScore(currentDigit)
                    this.playerHasNewMaxScore = true
                }
                currentDigit += 1
                viewModel.setNumberToGuess(getRandomNumber(currentDigit))
                hide(binding.guessNumberGroup)
                show(binding.showNumberGroup)
                viewModel.startCountdown()
            }else{
                showResultMessage(binding.playResultMessageError)
                if(userViewModel.isUserLogged.value!! && this.playerHasNewMaxScore) {
                    UserStorage.updateScore(userViewModel.maxScore.value!!, userViewModel.uid.value!!)
                }
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

        if(userViewModel.isUserLogged.value!! && this.playerHasNewMaxScore) {
            UserStorage.updateScore(userViewModel.maxScore.value!!, userViewModel.uid.value!!)
        }
    }

    private fun getRandomNumber(digits: Int): String {
        var n = ""
        repeat(digits){
            n += if(it == 1)
                (1..9).random().toString()
            else
                (0..9).random().toString()
        }
        return n
    }

    private val show = { v: Group -> v.visibility = View.VISIBLE }
    private val hide = { v: Group -> v.visibility = View.GONE }

    private fun showResultMessage(resMsg: Chip) {
        resMsg.visibility = View.VISIBLE
        resMsg.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
        resMsg.postDelayed({
            resMsg.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
            resMsg.visibility = View.INVISIBLE
        },3000)
    }

}