package com.example.wevote.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.wevote.R
import com.example.wevote.activities.VoterActivity
import com.example.wevote.activities.VoterActivity.Companion.log_out
import com.example.wevote.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executor


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var mAuth:FirebaseAuth
    private var Biometric = false

    //biometric
    lateinit var executor: Executor
    lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    lateinit var promptInfo:androidx.biometric.BiometricPrompt.PromptInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        log_out = false

        //biometric
        executor = ContextCompat.getMainExecutor(activity!!)
        biometricPrompt = androidx.biometric.BiometricPrompt(activity!!,executor,object:androidx.biometric.BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                // if any error occur
                Toast.makeText(activity,"Some error has occurred",Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(activity,"Wrong FingerPrint",Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(activity,"Biometric Authentication Successful",Toast.LENGTH_SHORT).show()
                Biometric = true
            }
        })
        promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Login using FingerPrint or Face")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)


        binding.buttonLoginLogin.setOnClickListener {

            val email = binding.edEmailLogin.text.toString()
            val password = binding.edPasswordLogin.editText?.text.toString()


            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(activity, "Please fill the details", Toast.LENGTH_SHORT).show()
            }else{
                login(email,password)
            }

        }

        binding.tvForgotPasswordLogin.setOnClickListener {

            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_frogetPasswordFragment)

        }


    }

    private fun login(email: String, password: String) {

        if (Biometric) {
            activity?.let {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(it) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val verification =
                                mAuth.currentUser?.isEmailVerified // email verification
                            if (verification == true) {


                                val intent = Intent(activity, VoterActivity::class.java)
                                startActivity(intent)

                            } else {
                                Toast.makeText(
                                    activity,
                                    "Please verify your email",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(activity, "User does not exist", Toast.LENGTH_SHORT)
                                .show()
                            view?.let { it1 ->
                                Navigation.findNavController(it1)
                                    .navigate(R.id.action_loginFragment_to_registerFragment)
                            }
                        }
                    }
            }

        }else{
            Toast.makeText(activity,"First do Biometric Authentication",Toast.LENGTH_SHORT).show()
        }
    }

}