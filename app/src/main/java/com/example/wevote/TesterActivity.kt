package com.example.wevote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.wevote.databinding.ActivityTesterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TesterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTesterBinding
    private lateinit var mdbRef: DatabaseReference
    private lateinit var mAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTesterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        mdbRef = FirebaseDatabase.getInstance().getReference("user")
            .child(userId)
        mdbRef.child("userImage").get().addOnSuccessListener {
            val url = it.child("url").value.toString()

            Glide.with(this).load(url).into(binding.imageView)

        }

    }
}