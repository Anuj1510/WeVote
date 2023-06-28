package com.example.wevote.fragments


import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.wevote.R
import com.example.wevote.activities.VoterActivity
import com.example.wevote.activities.VoterActivity.Companion.VoteCount
import com.example.wevote.activities.VoterActivity.Companion.detail_log_out
import com.example.wevote.activities.candidates
import com.example.wevote.data.Candidate
import com.example.wevote.databinding.FragmentCandidateDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.concurrent.Executor
import kotlin.system.exitProcess

class CandidateDetailsFragment : Fragment() {
    private lateinit var binding:FragmentCandidateDetailsBinding
    private lateinit var packageManager: PackageManager
    private val CAMERA_PERMISSION_CODE = 1
    private val CAMERA_REQUEST_CODE = 2
    lateinit var imageUri:Uri
    private var image:String? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var voteCount: Int = 0

    companion object {
        private const val PREF_VOTE_COUNT = "pref_vote_count"
    }


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
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

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

        voteCount = sharedPreferences.getInt(PREF_VOTE_COUNT, 0)

        binding.IVcamera.setOnClickListener {

            if(ContextCompat.checkSelfPermission(activity!!,android.Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED){
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent,CAMERA_REQUEST_CODE)
            }else{
                ActivityCompat.requestPermissions(
                    activity!!, arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }

        }

        binding.VoteToCandidate.setOnClickListener {
            if(VoteCount == 0){
                    val builder = MaterialAlertDialogBuilder(activity!!)
                    builder.setMessage("Do you want Give Vote to this Candidate")
                        .setPositiveButton("Yes") { _, _ ->
                                Toast.makeText(
                                    activity,
                                    "Thank You for giving your precious vote to us",
                                    Toast.LENGTH_SHORT
                                ).show()

                            VoteCount++
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
            }else{
                Toast.makeText(activity,"You have already given the vote",Toast.LENGTH_SHORT).show()
            }
        }

    }

    // for camera
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent,CAMERA_REQUEST_CODE)
            }else{
                Toast.makeText(activity,"Oops permission denied",Toast.LENGTH_LONG).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CAMERA_REQUEST_CODE){
                val thumbNail:Bitmap = data!!.extras!!.get("data") as Bitmap
                binding.VoterImage.setImageBitmap(thumbNail)
            }
        }
    }

}