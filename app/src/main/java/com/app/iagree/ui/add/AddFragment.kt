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
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.*

import com.app.iagree.adaptor.PostAdaptor
import com.app.iagree.model.Post
import com.app.iagree.ui.dashboard.DashboardViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.add_fragment.*
import kotlinx.android.synthetic.main.add_fragment.view.*
import xyz.hanks.library.bang.SmallBangView
import java.util.concurrent.TimeUnit

class AddFragment : Fragment() {

    private lateinit var profileID: String
    private lateinit var firebaseUser: FirebaseUser


    private var postAdaptor: PostAdaptor? = null
    private var postList: MutableList<Post>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.add_fragment, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        profileID = firebaseUser.uid
        root.add_ads_btn_add_fragment!!.visibility = View.GONE

        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(profileID)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child("phone").value == ""){
                    linear_layout_add_fragment?.visibility = View.VISIBLE
                    //add_ads_btn_add_fragment?.setBackgroundColor(resources.getColor(R.color.black))
                    add_event_btn_add_fragment?.setBackgroundColor(resources.getColor(R.color.black))
                    root.add_ads_btn_add_fragment?.isClickable = false
                    root.add_event_btn_add_fragment?.isClickable = false
                    waiting_add_fragment?.visibility = View.GONE
                }

                else{
                    linear_layout_add_fragment?.visibility = View.GONE
                    //add_ads_btn_add_fragment?.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    add_event_btn_add_fragment?.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    root.add_ads_btn_add_fragment?.isClickable = true
                    root.add_event_btn_add_fragment?.isClickable = true
                    waiting_add_fragment?.visibility = View.GONE
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


//        var recyclerView: RecyclerView? = null
//        recyclerView = root.findViewById(R.id.topPostRecyclerView)
//        val linearLayoutManager = LinearLayoutManager(context)
//        linearLayoutManager.reverseLayout = true
//        linearLayoutManager.stackFromEnd = true
//        recyclerView?.layoutManager = linearLayoutManager
//        recyclerView?.setHasFixedSize(true)
//        recyclerView?.itemAnimator = DefaultItemAnimator()
//
//        postList = ArrayList()
//        postAdaptor = context?.let { PostAdaptor(it,postList as ArrayList<Post>) }
//        recyclerView?.adapter = postAdaptor
//
//        topPost()

        //
        root.add_post_btn_add_fragment.setOnClickListener {
            val i = Intent(context,AddPostActivity::class.java)
            startActivity(i)
        }

        root.phoneAuth_add_fragment?.setOnClickListener {
            startActivity(Intent(context,PhoneAuthActivity::class.java))
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




        root.add_ads_btn_add_fragment?.setOnClickListener{

                startActivity(Intent(context,AddAdsActivity::class.java))

        }

        root.add_event_btn_add_fragment?.setOnClickListener{
            startActivity(Intent(context,AddEventsActivity::class.java))
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

    private fun topPost(){
        val ref = FirebaseDatabase.getInstance().reference.child("Likes")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                var a = 0
                var s = ""
                var b = 0
                for (snapshot in p0.children){
                    a = snapshot.childrenCount.toInt()
                    if (a>b){
                        b = a
                        s = snapshot.key.toString()
                    }
                }

                val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
                postRef.addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(goti: DataSnapshot) {
                        (postList as ArrayList<Post>).clear()
                        for(sex in goti.children){
                            val post = sex.getValue(Post::class.java)
                            if (post!!.getPostID() == s){
                                (postList as ArrayList<Post>).add(post)
                            }
                            postAdaptor!!.notifyDataSetChanged()

                        }

                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }
                })


            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}
