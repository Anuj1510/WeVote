package com.example.wevote.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.wevote.R
import com.example.wevote.databinding.FragmentAccountOptionsBinding

class AccountOptionsFragment : Fragment() {

    private lateinit var binding: FragmentAccountOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountOptionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRegisterAccountOptions.setOnClickListener {

            Navigation.findNavController(view).navigate(R.id.action_accountOptionsFragment_to_registerFragment)

        }

        binding.buttonLoginAccountOptions.setOnClickListener {

            Navigation.findNavController(view).navigate(R.id.action_accountOptionsFragment_to_loginFragment)

        }

    }

}