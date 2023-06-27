package com.example.wevote.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wevote.R
import com.example.wevote.activities.VoterActivity.Companion.detail_log_out
import com.example.wevote.activities.candidates
import com.example.wevote.adapters.CandidateAdapter
import com.example.wevote.data.Candidate
import com.example.wevote.databinding.FragmentCandidateBinding


class CandidateFragment : Fragment() {
    private lateinit var binding: FragmentCandidateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCandidateBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detail_log_out = false



//        val adapter = CandidateAdapter(candidate)
//        binding.candidateListRecyclerView.adapter = adapter
//        binding.candidateListRecyclerView.layoutManager = LinearLayoutManager(activity)

        val candidateList = view.findViewById<RecyclerView>(R.id.candidate_list_RecyclerView).apply {

            layoutManager = LinearLayoutManager(activity)

            adapter = CandidateAdapter{

                findNavController().navigate(CandidateFragmentDirections.actionCandidateFragmentToCandidateDetailsFragment(it.id))

            }
            setHasFixedSize(true)

        }

        (candidateList.adapter as CandidateAdapter).submitList(candidates)



    }


}