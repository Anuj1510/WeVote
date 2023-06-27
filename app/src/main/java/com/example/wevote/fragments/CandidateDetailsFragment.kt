package com.example.wevote.fragments


import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.Navigation
import com.example.wevote.R
import com.example.wevote.activities.VoterActivity.Companion.detail_log_out
import com.example.wevote.activities.candidates
import com.example.wevote.data.Candidate
import com.example.wevote.databinding.FragmentCandidateDetailsBinding

class CandidateDetailsFragment : Fragment() {
    private lateinit var binding:FragmentCandidateDetailsBinding
    private lateinit var packageManager: PackageManager
    private val PERMISSION_CODE = 1000
    lateinit var imageUri:Uri


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCandidateDetailsBinding.inflate(layoutInflater)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detail_log_out = true
        packageManager = requireContext().packageManager

        if(detail_log_out){
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
                Navigation.findNavController(view).navigate(R.id.action_candidateDetailsFragment_to_candidateFragment)
            }
        }

        var candidate:Candidate? = null
        arguments?.let { it->

            val args = CandidateDetailsFragmentArgs.fromBundle(it)
            candidate = candidates.find { args.id == it.id }

        }



        candidate?.let {
            with(it){
                binding.CandidateName.text = CandidateName
                binding.CandidateParty.text = PartyName
                binding.CandidateDescription.text = longDescription
                binding.CandidateProfile.setImageResource(imageId)
            }
        }

    }

}