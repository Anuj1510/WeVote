package com.example.wevote.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import com.example.wevote.R
import com.example.wevote.activities.VoterActivity.Companion.log_out
import com.example.wevote.data.User
import com.example.wevote.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var storageRef: FirebaseStorage
    private var uri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance()
        log_out = false

        val gallery = registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            binding.IVProfileImage.setImageURI(it)
            if (it != null) {
                uri = it
            }
        })


        binding.IVcamera.setOnClickListener {
            gallery.launch("image/*")
        }

        binding.buttonRegisterRegister.setOnClickListener {

            val firstName = binding.edFirstNameRegister.text.toString()
            val lastName = binding.edLastNameRegister.text.toString()
            val email = binding.edEmailRegister.text.toString()
            val password = binding.edPasswordRegister.editText?.text.toString()
            val phone = binding.edPhoneNumberRegister.text.toString()
            val phonenumber = phone.toLong()

            if(firstName.isEmpty()|| lastName.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || uri == null){
                Toast.makeText(activity, "Please fill the details", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(activity, "Details filled", Toast.LENGTH_SHORT).show()
                signUp(email,password,firstName,lastName,phonenumber, uri!!)
            }
        }


    }

    private fun signUp(
        email: String, password: String, firstName: String, lastName: String, phonenumber: Long,
        uri: Uri
    ) {
        activity?.let {
            mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // ye check karega ki email id already register hai ya nahi
                        val signInMethods = task.result?.signInMethods // List of sign-in methods for the email ID
                        val isEmailRegistered = signInMethods?.isNotEmpty() ?: false

                        if (isEmailRegistered) {
                            // Email ID is already registered
                            Toast.makeText(context, "Email ID is already registered", Toast.LENGTH_SHORT).show()
                        } else {
                            // agr not register then proceed
                            mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(it) { Task ->
                                    if (Task.isSuccessful) {
                                        // Sign in success, update UI with the sig6ned-in user's information
                                        mAuth.currentUser?.sendEmailVerification() // email verification
                                            ?.addOnSuccessListener {
                                                Toast.makeText(activity,"Please verify your email",Toast.LENGTH_SHORT).show()
                                            }
                                            ?.addOnFailureListener {
                                                Toast.makeText(activity,"Some error occurred",Toast.LENGTH_SHORT).show()
                                            }
                                        addUserToDatabase(email, mAuth.currentUser?.uid!!,firstName,lastName,phonenumber)
                                        view?.let { it1 -> Navigation.findNavController(it1).navigate(R.id.action_registerFragment_to_loginFragment) }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(activity, "Some error occurred. Please check your internet connection", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    } else {
                        // Error occurred while checking the email ID
                        Toast.makeText(context, "Error occurred while checking email ID", Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }

    private fun addUserToDatabase(
        email: String,
        uid: String,
        firstName: String,
        lastName: String,
        PhoneNumber: Long
    ) {

        storageRef.getReference("images").child(System.currentTimeMillis().toString())
            .putFile(uri!!)
            .addOnSuccessListener {task->

                task.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener {
                        val mapImage = mapOf(
                            "url" to it.toString()
                        )
                        mDbRef = FirebaseDatabase.getInstance().getReference()

                        mDbRef.child("user").child(uid).setValue(User(firstName,lastName,email,PhoneNumber, uid,mapImage))
                    }

            }



    }

}