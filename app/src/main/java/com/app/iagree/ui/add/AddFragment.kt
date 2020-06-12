package com.app.iagree.ui.add

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.renderscript.Sampler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.app.iagree.AddPostActivity

import com.app.iagree.R
import com.app.iagree.ui.dashboard.DashboardViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.add_fragment.view.*
import xyz.hanks.library.bang.SmallBangView

class AddFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.add_fragment, container, false)
        root.add_post_btn_add_fragment.setOnClickListener {
            val i = Intent(context,AddPostActivity::class.java)
            startActivity(i)
        }

        getQuote(root.quote)
        isLikes(root.like_quote)
        numberOfLikes(root.total_likes_quote)

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        root.like_quote.setOnClickListener {
            if (root.like_quote.isSelected){
                root.like_quote.isSelected = false

                FirebaseDatabase.getInstance().reference.child("quoteLikes")
                    .child(firebaseUser!!.uid).removeValue()

            }else{
                root.like_quote.isSelected = true
                root.like_quote.likeAnimation(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {

                    }
                })

                FirebaseDatabase.getInstance().reference.child("quoteLikes")
                    .child(firebaseUser!!.uid).setValue(true)


            }
        }

        return root
    }

    private fun getQuote(textView: TextView){
        val ref = FirebaseDatabase.getInstance().reference.child("Quote")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                textView.text = p0.child("quote").value.toString()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun numberOfLikes(like:TextView){
        val ref = FirebaseDatabase.getInstance().reference.child("quoteLikes")
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

    private fun isLikes(likeButton: SmallBangView){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("quoteLikes")
        ref.addValueEventListener(object :ValueEventListener{
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

}
