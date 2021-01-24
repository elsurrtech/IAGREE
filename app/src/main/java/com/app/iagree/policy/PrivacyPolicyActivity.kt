package com.app.iagree.policy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.iagree.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_privacy_policy.*

class PrivacyPolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        btnBack_privacyPolicy.setOnClickListener {
            onBackPressed()
        }


    }

    private fun online(){
        val ref = FirebaseDatabase.getInstance().reference.child("OnlineUsers")
        ref.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue("true")
    }


}