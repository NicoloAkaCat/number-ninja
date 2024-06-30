package dev.nicoloakacat.numberninja.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dev.nicoloakacat.numberninja.databinding.FragmentPlayBinding


class PlayFragment : Fragment() {

    private lateinit var binding: FragmentPlayBinding
    private val viewModel: PlayViewModel by viewModels()
    private var showNumber = false

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
        viewModel.setNumberToGuess(getRandomNumber(1))
        binding.playBtn.setOnClickListener {
            binding.introGroup.visibility = View.GONE
            binding.gameGroup.visibility = View.VISIBLE
            viewModel.startCountdown()
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
            n += (0..10).random().toString()
        }
        return n
    }

}