package dev.nicoloakacat.numberninja.ui.rankings

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.nicoloakacat.numberninja.R
import dev.nicoloakacat.numberninja.db.UserData
import dev.nicoloakacat.numberninja.db.UserStorage
import dev.nicoloakacat.numberninja.databinding.FragmentRankingsBinding
import dev.nicoloakacat.numberninja.databinding.ItemRankingsBinding
import dev.nicoloakacat.numberninja.getFlagUri

class RankingAdapter(private val pContext: Context, private val dataSet: MutableList<UserData>) : RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {
    inner class RankingViewHolder(val view: ItemRankingsBinding) : RecyclerView.ViewHolder(view.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val binding = ItemRankingsBinding.inflate(LayoutInflater.from(pContext), parent, false)
        return  RankingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val binding = holder.view
        binding.playerCardFlag.setImageDrawable(ResourcesCompat.getDrawable(
            pContext.resources,
            pContext.resources.getIdentifier(getFlagUri(dataSet[position].nationality!!), null, null),
            null
        ))
        binding.playerCardName.text = dataSet[position].name
        binding.playerCardScore.text = dataSet[position].maxScore.toString()
        binding.playerCardScore.setTextAppearance(
            if(position in 0..2) R.style.scoreBig else R.style.scoreSmall
        )
    }

    override fun getItemCount(): Int = dataSet.size
}

class RankingsFragment : Fragment() {

    private lateinit var binding: FragmentRankingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rankingsViewModel: RankingsViewModel by viewModels()

        binding = FragmentRankingsBinding.inflate(inflater, container, false)
        binding.viewModel = rankingsViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO errori
        val users = UserStorage.findAll()
        val rankingAdapter = RankingAdapter(requireContext(), users)

        val recyclerView = binding.rankingsRv
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = rankingAdapter
    }

}