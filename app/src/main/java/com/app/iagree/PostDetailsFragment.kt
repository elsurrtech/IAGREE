package com.app.iagree

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.adaptor.MyImagesAdaptor
import com.app.iagree.adaptor.PostAdaptor
import com.app.iagree.model.Post
import com.app.iagree.ui.dashboard.DashboardFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.*

/**
 * A simple [Fragment] subclass.
 */
class PostDetailsFragment : Fragment() {

    private var postAdaptor: PostAdaptor? = null
    private var postList: MutableList<Post>? = null
    private var postID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_post_details, container, false)

        val preferences = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (preferences!=null){
            postID = preferences.getString("postID","").toString()
        }

        var recyclerView: RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView_post_details_fragment)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager=LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager

        postList=ArrayList()!!
        postAdaptor = context?.let { PostAdaptor(it,postList as ArrayList<Post>) }
        recyclerView.adapter = postAdaptor

        retrievePosts()
        return view
    }

    private fun retrievePosts(){
        val postRef = FirebaseDatabase.getInstance().reference
            .child("Posts").child(postID)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    postList?.clear()
                    val post = p0.getValue(Post::class.java)
                    postList!!.add(post!!)
                    postAdaptor!!.notifyDataSetChanged()
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }



}
