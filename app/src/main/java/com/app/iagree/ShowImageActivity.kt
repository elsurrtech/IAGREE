package com.app.iagree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_show_image.*

class ShowImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_image)

        val messageUrl = intent.getStringExtra("messageImage")

        val imageView = findViewById<ImageView>(R.id.image_activity_show_image)
        Picasso.get().load(messageUrl).into(imageView)
        btnBack_showImageActivity.setOnClickListener {
            onBackPressed()
        }

    }

    private fun online(){
        val ref = FirebaseDatabase.getInstance().reference.child("OnlineUsers")
        ref.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue("true")
    }


}