package dev.nicoloakacat.numberninja.ui.rankings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dev.nicoloakacat.numberninja.databinding.FragmentRankingsBinding

class RankingsFragment : Fragment() {

    private var _binding: FragmentRankingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rankingsViewModel: RankingsViewModel by viewModels()

        _binding = FragmentRankingsBinding.inflate(inflater, container, false)
        binding.viewModel = rankingsViewModel

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}