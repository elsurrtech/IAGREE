package com.app.iagree.settings

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.app.iagree.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_poll.*
import kotlinx.android.synthetic.main.add_fragment.view.*
import xyz.hanks.library.bang.SmallBangView

class PollActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poll)

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        isLikesMessaging(image_poll_message)
        isLikesFilter(image_poll_filters)
        isLikesVideo(image_poll_video)
        isLikesVideoCall(image_poll_videoCall)

        numberOfLikesMessaging(text_poll_message)
        numberOfLikeVideos(text_poll_video)
        numberOfLikesVideoCall(text_poll_videoCall)
        numberOfLikesFilters(text_poll_filters)

        btnBack_poll.setOnClickListener {
            onBackPressed()
        }

        //Message Poll
        image_poll_message.setOnClickListener {
            if (image_poll_message.isSelected){
                image_poll_message.isSelected = false

                FirebaseDatabase.getInstance().reference.child("Poll").child("Messaging")
                    .child(firebaseUser!!.uid).removeValue()

            }else{
                image_poll_message.isSelected = true
                image_poll_message.likeAnimation(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {

                    }
                })

                FirebaseDatabase.getInstance().reference.child("Poll").child("Messaging")
                    .child(firebaseUser!!.uid).setValue(true)


            }
        }

        //Video Poll
        image_poll_video.setOnClickListener {
            if (image_poll_video.isSelected){
                image_poll_video.isSelected = false

                FirebaseDatabase.getInstance().reference.child("Poll").child("Video")
                    .child(firebaseUser!!.uid).removeValue()

            }else{
                image_poll_video.isSelected = true
                image_poll_video.likeAnimation(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {

                    }
                })

                FirebaseDatabase.getInstance().reference.child("Poll").child("Video")
                    .child(firebaseUser!!.uid).setValue(true)


            }
        }

        //VideoCall Poll
        image_poll_videoCall.setOnClickListener {
            if (image_poll_videoCall.isSelected){
                image_poll_videoCall.isSelected = false

                FirebaseDatabase.getInstance().reference.child("Poll").child("AudioVideoCall")
                    .child(firebaseUser!!.uid).removeValue()

            }else{
                image_poll_videoCall.isSelected = true
                image_poll_videoCall.likeAnimation(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {

                    }
                })

                FirebaseDatabase.getInstance().reference.child("Poll").child("AudioVideoCall"
                )
                    .child(firebaseUser!!.uid).setValue(true)

            }
        }

        //StoryFilters Poll
        image_poll_filters.setOnClickListener {
            if (image_poll_filters.isSelected){
                image_poll_filters.isSelected = false

                FirebaseDatabase.getInstance().reference.child("Poll").child("Filters")
                    .child(firebaseUser!!.uid).removeValue()

            }else{
                image_poll_filters.isSelected = true
                image_poll_filters.likeAnimation(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {

                    }
                })

                FirebaseDatabase.getInstance().reference.child("Poll").child("Filters"
                )
                    .child(firebaseUser!!.uid).setValue(true)

            }
        }

        suggestionSubmitButton.setOnClickListener {
            if (suggestionBox.text.toString() == ""){
                Toast.makeText(this,"Please Enter Something",Toast.LENGTH_SHORT).show()
            }else{
                val a = suggestionBox.text.toString()
                val ref = FirebaseDatabase.getInstance().reference.child("Poll").child("suggestions")
                val map = HashMap<String,Any?>()
                map["suggestion"] = a
                ref.child(firebaseUser!!.uid).push().setValue(map).addOnCompleteListener {
                    if (it.isSuccessful){
                        suggestionBox!!.text.clear()
                        Toast.makeText(this,"We appreciate your support",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this,"OOPS! Something went wrong",Toast.LENGTH_LONG).show()
                    }
                }
            }


        }



    }

    private fun numberOfLikesMessaging(like: TextView){
        val ref = FirebaseDatabase.getInstance().reference.child("Poll").child("Messaging")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    like.text = p0.childrenCount.toString()
                }else{
                    like.text = "0"
                }
            }
        })
    }

    private fun numberOfLikeVideos(like: TextView){
        val ref = FirebaseDatabase.getInstance().reference.child("Poll").child("Video")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    like.text = p0.childrenCount.toString()
                }else{
                    like.text = "0"
                }
            }
        })
    }

    private fun numberOfLikesVideoCall(like: TextView){
        val ref = FirebaseDatabase.getInstance().reference.child("Poll").child("AudioVideoCall")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    like.text = p0.childrenCount.toString()
                }else{
                    like.text = "0"
                }
            }
        })
    }

    private fun numberOfLikesFilters(like: TextView){
        val ref = FirebaseDatabase.getInstance().reference.child("Poll").child("Filters")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    like.text = p0.childrenCount.toString()
                }else{
                    like.text = "0"
                }
            }
        })
    }

    private fun isLikesMessaging(likeButton: SmallBangView){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("Poll").child("Messaging")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(firebaseUser!!.uid).exists()){
                    likeButton.isSelected = true
                    likeButton.tag = "Liked"
                }else{
                    likeButton.tag = "Like"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun isLikesVideo(likeButton: SmallBangView){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("Poll").child("Video")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(firebaseUser!!.uid).exists()){
                    likeButton.isSelected = true
                    likeButton.tag = "Liked"
                }else{
                    likeButton.tag = "Like"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun isLikesVideoCall(likeButton: SmallBangView){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("Poll").child("AudioVideoCall")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(firebaseUser!!.uid).exists()){
                    likeButton.isSelected = true
                    likeButton.tag = "Liked"
                }else{
                    likeButton.tag = "Like"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun isLikesFilter(likeButton: SmallBangView){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("Poll").child("Filters")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(firebaseUser!!.uid).exists()){
                    likeButton.isSelected = true
                    likeButton.tag = "Liked"
                }else{
                    likeButton.tag = "Like"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun online(){
        val ref = FirebaseDatabase.getInstance().reference.child("OnlineUsers")
        ref.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue("true")
    }


}