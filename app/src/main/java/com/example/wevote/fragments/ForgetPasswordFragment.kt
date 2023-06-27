package com.example.wevote.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.wevote.R
import com.example.wevote.activities.VoterActivity.Companion.log_out
import com.example.wevote.databinding.FragmentForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth


class ForgetPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgetPasswordBinding
    private lateinit var mAuth:FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgetPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        log_out = false


        // jub tuk email verify waali functionality on nahi hogi tub tuk reset password kaam nahi karega
        binding.buttonResetReset.setOnClickListener {
            // to reset password
            val vEmail = binding.edEmailReset.text.toString()
            mAuth.sendPasswordResetEmail(vEmail)
                .addOnSuccessListener {
                    Toast.makeText(activity,"Please check your email", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(activity,"Either email does not exist\nor Some error has occurred", Toast.LENGTH_LONG).show()
                }
        }

    }

}