package com.app.iagree.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.app.iagree.R
import com.app.iagree.adaptor.MessagePagesAdaptor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_message_container.*

class MessageContainerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_container)

        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = MessagePagesAdaptor(supportFragmentManager)
        viewPager.currentItem = 0
        r2_message.setupWithViewPager(viewPager)


    }



    private fun online(){
        val ref = FirebaseDatabase.getInstance().reference.child("OnlineUsers")
        ref.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue("true")
    }



}