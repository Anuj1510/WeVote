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
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.wevote.R
import com.example.wevote.activities.VoterActivity
import com.example.wevote.activities.VoterActivity.Companion.VoteCount
import com.example.wevote.activities.VoterActivity.Companion.detail_log_out
import com.example.wevote.activities.candidates
import com.example.wevote.data.Candidate
import com.example.wevote.data.User
import com.example.wevote.data.Voter
import com.example.wevote.databinding.FragmentCandidateDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executor
import kotlin.system.exitProcess

class CandidateDetailsFragment : Fragment() {
    private lateinit var binding:FragmentCandidateDetailsBinding
    private lateinit var packageManager: PackageManager
    private val CAMERA_PERMISSION_CODE = 1
    private val CAMERA_REQUEST_CODE = 2
    lateinit var imageUri:Uri
    private var image:String? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var storageRef: FirebaseStorage
    private var uri: Uri? = null
    var party_name:String? = null

    companion object {
        lateinit var sharedPreferences: SharedPreferences
        var voteCount: Int = 0
        var setImage:Boolean = false
        const val PREF_VOTE_COUNT = "pref_vote_count"
        const val PREF_SET_IMAGE = "pref_set_image"
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

        mAuth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance()

        detail_log_out = true
        packageManager = requireContext().packageManager
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        voteCount = sharedPreferences.getInt(PREF_VOTE_COUNT, 0)
        setImage = sharedPreferences.getBoolean(PREF_SET_IMAGE, false)

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
                party_name = PartyName
                binding.CandidateDescription.text = longDescription
                binding.CandidateProfile.setImageResource(imageId)
            }
        }

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

//        val gallery = registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback {
//                binding.IVcamera.setImageURI(it)
//                if (it != null) {
//                    uri = it
//                }
//            })

        binding.VoteToCandidate.setOnClickListener {
            if(voteCount == 0 && setImage){
                    val builder = MaterialAlertDialogBuilder(activity!!)
                    builder.setMessage("Do you want Give Vote to this Candidate")
                        .setPositiveButton("Yes") { _, _ ->

                            val partyName = party_name
                            addUsertoDataBase(partyName,mAuth.currentUser!!.uid)

                                Toast.makeText(
                                    activity,
                                    "Thank You for giving your precious vote to us",
                                    Toast.LENGTH_SHORT
                                ).show()

                            voteCount++
                            sharedPreferences.edit().putInt(PREF_VOTE_COUNT, voteCount).apply()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
            }else{
                Toast.makeText(activity,"Either you have not click your photo or you have already given the vote",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun addUsertoDataBase(partyName: String?, uid: String) {
        if(uri == null){
            Toast.makeText(activity,"No image",Toast.LENGTH_SHORT).show()
        }else{
            storageRef.getReference("voterImages").child(System.currentTimeMillis().toString())
                .putFile(uri!!)
                .addOnSuccessListener {task->

                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {
                            val mapImage = mapOf(
                                "url" to it.toString()
                            )
                            mDbRef = FirebaseDatabase.getInstance().getReference()

                            mDbRef.child("Voter").child(uid).setValue(Voter( uid,mapImage,partyName))
                        }

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
                val imageBitmap: Bitmap? = data?.extras?.get("data") as? Bitmap
                uri = imageBitmap?.let { saveImageToStorage(it) }
                val thumbNail:Bitmap = data!!.extras!!.get("data") as Bitmap
                binding.VoterImage.setImageBitmap(thumbNail)
                setImage = true
                sharedPreferences.edit().putBoolean(PREF_SET_IMAGE, setImage).apply()
            }
        }
    }

    private fun saveImageToStorage(bitmap: Bitmap): Uri {
        val imageFileName = "voter_image_${System.currentTimeMillis()}"
        val imageFile = File(activity?.cacheDir, imageFileName)

        FileOutputStream(imageFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }

        return imageFile.toUri()
    }

}