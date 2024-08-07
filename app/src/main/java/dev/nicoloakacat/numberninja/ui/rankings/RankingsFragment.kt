package dev.nicoloakacat.numberninja.ui.rankings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.nicoloakacat.numberninja.R
import dev.nicoloakacat.numberninja.db.UserData
import dev.nicoloakacat.numberninja.db.UserStorage
import dev.nicoloakacat.numberninja.databinding.FragmentRankingsBinding
import dev.nicoloakacat.numberninja.databinding.ItemRankingsBinding
import dev.nicoloakacat.numberninja.getFlagUri
import dev.nicoloakacat.numberninja.showResultMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RankingAdapter(
    private val pContext: Context,
    private val dataSet: List<UserData>
) : RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {
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
    private val viewModel: RankingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRankingsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.rankingsRv
        recyclerView.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            try {
                val users = UserStorage.findAll()
                viewModel.setUsers(users)
            }catch (e: Exception){
                viewModel.setShowError(true)
                Log.e("RANKINGS_FRAGMENT", "An error occurred when retrieving data from DB")
            }
        }
        viewModel.users.observe(viewLifecycleOwner){
            val rankingAdapter = RankingAdapter(requireContext(), viewModel.users.value!!)
            recyclerView.adapter = rankingAdapter
        }
        viewModel.showError.observe(viewLifecycleOwner) {
            if(it)
                showResultMessage(binding.rankingsErrorMessage, requireContext())
        }
    }

}