package com.example.wevote.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.wevote.R
import com.example.wevote.data.User
import com.example.wevote.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var mdbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userList : ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        userList = ArrayList()
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        mdbRef = FirebaseDatabase.getInstance().getReference()


        mdbRef.child("user").addValueEventListener(object : ValueEventListener {
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
                    binding.VoterEmail.text = "Email :- ${userList[0].email}"
                    binding.VoterName.text = "Name :- ${userList[0].firstName} ${userList[0].lastName}"
                    binding.VoterPhone.text = "Phone no :- ${userList[0].PhoneNumber}"
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        mdbRef = FirebaseDatabase.getInstance().getReference("user").child(userId)

        mdbRef.child("userImage").get().addOnSuccessListener {
            val url = it.child("url").value.toString()

            Glide.with(this).load(url).into(binding.profileImage)
        }

        binding.Exitbtn.setOnClickListener {
            finishAffinity()
        }

    }
}