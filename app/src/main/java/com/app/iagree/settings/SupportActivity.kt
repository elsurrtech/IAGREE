package com.app.iagree.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.iagree.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_support.*


class SupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        insta_quiqle.setOnClickListener {
            val x = Uri.parse("http://instagram.com/_u/quiqle")
            val i = Intent(Intent.ACTION_VIEW,x)
            i.setPackage("com.instagram.android")

            try {
                startActivity(i)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/quiqle")
                    )
                )
            }

        }

        insta.setOnClickListener {
            val x = Uri.parse("http://instagram.com/_u/piktofill")
            val i = Intent(Intent.ACTION_VIEW,x)
            i.setPackage("com.instagram.android")

            try {
                startActivity(i)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/piktofill")
                    )
                )
            }

        }

        btnBack_support.setOnClickListener {
            onBackPressed()
        }

        twitter_quiqle.setOnClickListener {
            val url = "https://twitter.com/QuiqleC"
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