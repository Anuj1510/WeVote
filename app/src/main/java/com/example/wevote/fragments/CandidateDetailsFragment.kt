package com.example.wevote.fragments


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
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
import com.example.wevote.activities.VoterActivity.Companion.VoteCount
import com.example.wevote.activities.VoterActivity.Companion.detail_log_out
import com.example.wevote.activities.candidates
import com.example.wevote.data.Candidate
import com.example.wevote.data.User
import com.example.wevote.data.Voter
import com.example.wevote.databinding.FragmentCandidateDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream

class CandidateDetailsFragment : Fragment() {
    private lateinit var binding:FragmentCandidateDetailsBinding
    private lateinit var packageManager: PackageManager
    private val CAMERA_PERMISSION_CODE = 1
    private val CAMERA_REQUEST_CODE = 2
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var storageRef: FirebaseStorage
    private var ImageClickuri: Uri? = null
    private var Aaadharuri:Uri? = null
    var party_name:String? = null
    var voter_email:String?=null
    var voter_name:String? = null
    var voter_profile_image:String? = null
    private lateinit var userList : ArrayList<User>

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
        userList = ArrayList()


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

        // for aadharcard
        val gallery = registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                binding.AadharCardPhoto.setImageURI(it)
                if (it != null) {
                    Aaadharuri = it
                }
                else{
                    Toast.makeText(activity,"Image size is big",Toast.LENGTH_SHORT).show()
                }
            })

        // for aadharcard
        binding.IVcameraAadhar.setOnClickListener {
            gallery.launch("image/*")
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

        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //currentUser = snapshot.getValue(User::class.java)

                userList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if(mAuth.currentUser?.uid == currentUser?.uid){
                        userList.add(currentUser!!)
                    }
                }


                if(userList.isNotEmpty()){
                   voter_email = userList[0].email
                    voter_name = userList[0].firstName + " " + userList[0].lastName
                    voter_profile_image = userList[0].UserImage.toString()
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



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
            if(setImage && voteCount == 0){
                val builder = MaterialAlertDialogBuilder(activity!!)
                builder.setMessage("Do you want Give Vote to this Candidate")
                    .setPositiveButton("Yes") { _, _ ->

                        val partyName = party_name
                        addUsertoDataBase(partyName,mAuth.currentUser!!.uid)
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
        if(ImageClickuri == null){
            voteCount = 0
            Toast.makeText(activity,"Please click your image",Toast.LENGTH_SHORT).show()
        }
        else if(Aaadharuri == null){
            voteCount = 0
            Toast.makeText(activity,"Please upload your Aadhar Card photo",Toast.LENGTH_SHORT).show()
        }
        else {
            voteCount++
            sharedPreferences.edit().putInt(PREF_VOTE_COUNT, voteCount).apply()
            Toast.makeText(
                activity,
                "Thank You for giving your precious vote to us",
                Toast.LENGTH_SHORT
            ).show()
            //mDbRef = FirebaseDatabase.getInstance().getReference("user").child(userId)
                    val uri1 = ImageClickuri
                    val uri2 = Aaadharuri
                    val imageRefs = mutableListOf<String>()
                    for (imageUri in listOf(uri1, uri2)) {
                        if (imageUri != null) {
                            storageRef.getReference("voterImages")
                                .child(System.currentTimeMillis().toString())
                                .putFile(imageUri)
                                .addOnSuccessListener { taskSnapshot ->
                                    // Get the download URL for the uploaded image
                                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                                        imageRefs.add(downloadUrl.toString())

                                        // Check if all images are uploaded
                                        if (imageRefs.size == 2) {
                                            // Create the data object with the download URLs and additional fields
                                            val data = Voter(uid,voter_name, imageRefs[1], voter_email, imageRefs[0], partyName,voter_profile_image)

                                            // Upload the data to Firebase Realtime Database
                                            val databaseRef =
                                                FirebaseDatabase.getInstance().getReference()
                                            databaseRef.child("Voter").child(uid).setValue(data)
                                                .addOnSuccessListener {
                                                    // Data uploaded successfully
                                                    Toast.makeText(
                                                        activity,
                                                        "Data uploaded successfully",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                .addOnFailureListener { e ->
                                                    // Handle the failure
                                                    Toast.makeText(
                                                        activity,
                                                        "Failed to upload data: ${e.message}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        }
                                    }
                                }
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
                ImageClickuri = imageBitmap?.let { saveImageToStorage(it) }
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