package dev.nicoloakacat.numberninja.ui.play

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dev.nicoloakacat.numberninja.R
import dev.nicoloakacat.numberninja.UserViewModel
import dev.nicoloakacat.numberninja.databinding.FragmentPlayBinding


class PlayFragment : Fragment() {

    private lateinit var binding: FragmentPlayBinding
    private val viewModel: PlayViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private var currentDigit: Int = 1

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
            val resultMsg: TextView = binding.playResultMessage
            resultMsg.visibility = View.VISIBLE
            val guess = binding.playGuessNumber.text.toString()
            binding.playGuessNumber.text?.clear()
            // close keyboard
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            // check guess
            if(viewModel.number.value == guess){
                resultMsg.text = resources.getString(R.string.play_guess_success)
                resultMsg.setTextColor(resources.getColor(R.color.black, requireActivity().theme))
                resultMsg.postDelayed({
                    resultMsg.visibility = View.INVISIBLE
                    //TODO animation
                }, 3000)
                userViewModel.setMaxScore(currentDigit)
                currentDigit += 1
                viewModel.setNumberToGuess(getRandomNumber(currentDigit))
                hide(binding.guessNumberGroup)
                show(binding.showNumberGroup)
                viewModel.startCountdown()
            }else{
                resultMsg.text = resources.getString(R.string.play_guess_failure)
                resultMsg.setTextColor(resources.getColor(R.color.purple_500, requireActivity().theme))
                resultMsg.postDelayed({
                    resultMsg.visibility = View.INVISIBLE
                    //TODO animation
                }, 3000)
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

}