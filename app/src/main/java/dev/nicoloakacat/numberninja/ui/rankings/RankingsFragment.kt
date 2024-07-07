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
import dev.nicoloakacat.numberninja.Nationalities
import dev.nicoloakacat.numberninja.R
import dev.nicoloakacat.numberninja.UserDB
import dev.nicoloakacat.numberninja.UserStorage
import dev.nicoloakacat.numberninja.databinding.FragmentRankingsBinding

class RankingAdapter(private val dataSet: MutableList<UserDB>) : RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {
    class RankingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerCardNameTextView: TextView = view.findViewById(R.id.player_card_name)
        val playerCardScoreTextView: TextView = view.findViewById(R.id.player_card_score)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.player_card_layout, parent, false)
        return  RankingViewHolder(layout)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        Log.d("USERS", dataSet[position].name.toString())

        val nationalityIndex = dataSet[position].nationality!!
        val nationalityFlag = Nationalities.entries[nationalityIndex].flag

        holder.playerCardNameTextView.text = dataSet[position].name + " " + nationalityFlag
        holder.playerCardScoreTextView.text = dataSet[position].maxScore.toString()
    }

    override fun getItemCount(): Int = dataSet.size
}

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val users = UserStorage.findAll()
        val rankingAdapter = RankingAdapter(users)

        val recyclerView = binding.topUsers
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = rankingAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}