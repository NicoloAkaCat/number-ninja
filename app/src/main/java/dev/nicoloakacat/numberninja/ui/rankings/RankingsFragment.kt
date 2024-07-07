package dev.nicoloakacat.numberninja.ui.rankings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.nicoloakacat.numberninja.R
import dev.nicoloakacat.numberninja.UserData
import dev.nicoloakacat.numberninja.UserStorage
import dev.nicoloakacat.numberninja.databinding.FragmentRankingsBinding

class RankingAdapter(private val dataSet: MutableList<UserData>) : RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {
    inner class RankingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerCardNameTextView: TextView = view.findViewById(R.id.player_card_name)
        val playerCardScoreTextView: TextView = view.findViewById(R.id.player_card_score)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.player_card_layout, parent, false)
        return  RankingViewHolder(layout)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        Log.d("USERS", dataSet[position].name.toString())

        //TODO add icon drawable
        holder.playerCardNameTextView.text = dataSet[position].name
        holder.playerCardScoreTextView.text = dataSet[position].maxScore.toString()
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
        val rankingAdapter = RankingAdapter(users)

        val recyclerView = binding.topUsers
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = rankingAdapter
    }

}