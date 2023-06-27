package com.example.wevote.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wevote.R
import com.example.wevote.activities.VoterActivity.Companion.log_out

class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        supportActionBar?.hide()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(log_out){
            finishAffinity()
        }

    }

}