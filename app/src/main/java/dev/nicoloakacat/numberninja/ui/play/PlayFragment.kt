package dev.nicoloakacat.numberninja.ui.play

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dev.nicoloakacat.numberninja.R
import dev.nicoloakacat.numberninja.databinding.FragmentPlayBinding


class PlayFragment : Fragment() {

    private lateinit var binding: FragmentPlayBinding
    private val viewModel: PlayViewModel by viewModels()
    private var currentDigit: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentDigit = 1
        viewModel.setNumberToGuess(getRandomNumber(currentDigit))
        binding.playBtn.setOnClickListener {
            binding.introGroup.visibility = View.GONE
            binding.showNumberGroup.visibility = View.VISIBLE
            viewModel.startCountdown()
        }
        viewModel.countdownProgress.observe(viewLifecycleOwner) {
            if(it == 0) {
                binding.showNumberGroup.visibility = View.GONE
                binding.guessGroup.visibility = View.VISIBLE
            }
        }
        binding.playGuessBtn.setOnClickListener {
            binding.playResultMessage.visibility = View.VISIBLE
            val guess = binding.playGuessNumber.text.toString()
            binding.playGuessNumber.text?.clear()
            // close keyboard
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            // check guess
            if(viewModel.number.value == guess){
                binding.playResultMessage.text = resources.getString(R.string.play_guess_success)
                binding.playResultMessage.setTextColor(resources.getColor(R.color.black, requireActivity().theme))
                binding.playResultMessage.postDelayed({
                    binding.playResultMessage.visibility = View.INVISIBLE
                    //TODO animation
                }, 3000)
                viewModel.setMaxScore(currentDigit)
                currentDigit += 1
                viewModel.setNumberToGuess(getRandomNumber(currentDigit))
                binding.guessGroup.visibility = View.GONE
                binding.showNumberGroup.visibility = View.VISIBLE
                viewModel.startCountdown()
            }else{
                binding.playResultMessage.text = resources.getString(R.string.play_guess_failure)
                binding.playResultMessage.setTextColor(resources.getColor(R.color.purple_500, requireActivity().theme))
                binding.playResultMessage.postDelayed({
                    binding.playResultMessage.visibility = View.INVISIBLE
                }, 3000)
                currentDigit = 1
                binding.guessGroup.visibility = View.GONE
                binding.introGroup.visibility = View.VISIBLE
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

}