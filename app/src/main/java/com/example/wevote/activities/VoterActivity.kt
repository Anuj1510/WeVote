package com.example.wevote.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.wevote.R
import com.example.wevote.data.Candidate
import com.example.wevote.databinding.ActivityVoterBinding
import com.google.firebase.auth.FirebaseAuth

var candidates = mutableListOf(
    Candidate(
        1, "Anuj Shahi",  R.drawable.candidate1,
        "हम अच्छे और सच्चे कर्मों पर विश्वास करते हैं",
        "Anuj Anshan Party(ASS)"
    ),
    Candidate(
        2, "Nisha Shahi",  R.drawable.candidate2,
        "हम सिर्फ कामचोरी पर विश्वास करते हैं",
        "Nisha KamChor Party(NKP)"
    ),
    Candidate(
        3, "Maya Shahi", R.drawable.candidate3,
        "हम केवल कड़ी मेहनत पर विश्वास करते हैं",
        "Maya HardWork Party(MHP)"
    ),
    Candidate(
        4, "Pooja Shahi", R.drawable.candidate4,
        "हम केवल विदेशियों पर विश्वास करते हैं",
        "Pooja Foreign Party(PFP)"
    ),
    Candidate(
        5, "Devendra Shahi", R.drawable.candidate5,
        "हम केवल मोटी चोरी पर विश्वास करते हैं",
        "Devendra Mota Party(DMP)"
    ),
    Candidate(
        6, "Himanshu Shahi", R.drawable.candidate6,
        "हम केवल आलस्य पर विश्वास करते हैं",
        "Himanshu Aalsi Party(HAP)"

    ),
    Candidate(
        7, "Prabhakar Singh", R.drawable.candidate7,
        "हम केवल व्लॉगिंग पर विश्वास करते हैं",
        "Prabhakar Vlog Party(PVP)"
    )
)


class VoterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVoterBinding
    private lateinit var mAuth:FirebaseAuth

    companion object{
         var log_out:Boolean = false
        var detail_log_out:Boolean = false
        var VoteCount:Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.logout -> {
                log_out = true
                mAuth.signOut()
                val intent = Intent(this,LoginRegisterActivity::class.java)
                startActivity(intent)
            }

            R.id.profile -> {
                val intent = Intent(this,ProfileActivity::class.java)
                startActivity(intent)
            }


        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(!detail_log_out){
            finishAffinity()
        }

    }

}