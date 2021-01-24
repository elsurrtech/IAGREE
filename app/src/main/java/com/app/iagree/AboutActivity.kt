package com.app.iagree

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.iagree.policy.PrivacyPolicyActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        btnBack_about.setOnClickListener{
            onBackPressed()
        }

        privacy_about.setOnClickListener {
            val i  = Intent(this,PrivacyPolicyActivity::class.java)
            i.putExtra("fromSetting","fromSetting")
            startActivity(i)

        }

        terms_about.setOnClickListener {
            val url = "https://piktofill.blogspot.com/p/terms-and-conditions.html"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        acknw_about.setOnClickListener {
            val url = "https://piktofill.blogspot.com/p/acknowledgments.html"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }


    }


    private fun online(){
        val ref = FirebaseDatabase.getInstance().reference.child("OnlineUsers")
        ref.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue("true")
    }

}