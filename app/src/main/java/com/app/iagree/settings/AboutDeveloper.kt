package com.app.iagree.settings

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.iagree.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_about_developer.*

class AboutDeveloper : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_developer)

        linkedin.setOnClickListener {
            val url = "https://www.linkedin.com/in/deepesh-sengar-b4032b140/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        btnBack_developer.setOnClickListener {
            onBackPressed()
        }



    }

    private fun online(){
        val ref = FirebaseDatabase.getInstance().reference.child("OnlineUsers")
        ref.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue("true")
    }


}