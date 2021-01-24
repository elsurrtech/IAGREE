package com.app.iagree.home

import android.os.Bundle
import android.renderscript.Sampler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agrawalsuneet.dotsloader.loaders.LazyLoader
import com.app.iagree.R
import com.app.iagree.adaptor.EventsAdaptor
import com.app.iagree.adaptor.PostAdaptor
import com.app.iagree.model.Post
import com.app.iagree.model.event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_info.*
import kotlinx.android.synthetic.main.fragment_memes.*
import kotlinx.android.synthetic.main.fragment_personal.*

//Events

class PersonalFragment : Fragment() {

    private var postAdaptor: PostAdaptor? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<String>? = null

    private var eventAdaptor : EventsAdaptor? = null
    private var eventList : MutableList<event>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_personal, container, false)

        var recyclerView: RecyclerView? = null
        recyclerView = view.findViewById(R.id.recyclerView_personal_fragment)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)

        eventList = ArrayList()
        eventAdaptor = context?.let { EventsAdaptor(it,eventList as ArrayList<event>) }
        recyclerView.adapter= eventAdaptor


        checkEvents()


        return view
    }

    private fun checkEvents(){

        val ref = FirebaseDatabase.getInstance().reference.child("Events")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    eventList!!.clear()
                    for (snapshot in p0.children){
                        val x = snapshot.getValue(event::class.java)
                        eventList!!.add(x!!)
                    }

                    eventAdaptor!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun checkFollowings() {

        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    (followingList as ArrayList<String>).clear()
                    for (snapshot in p0.children){
                        snapshot.key?.let { (followingList as ArrayList<String>).add(it) }
                    }

                    //retrievePosts()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun retrievePosts(){

        val postRef = FirebaseDatabase.getInstance().reference
            .child("Posts").orderByChild("postCategory").equalTo("Personal")

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                postList?.clear()

                for (snapsot in p0.children){
                    val post = snapsot.getValue(Post::class.java)



                    for (userid in (followingList as ArrayList<String>) ){
                        if (post!!.getPublisher() == userid){

                            postList!!.add(post)

                            postAdaptor!!.notifyDataSetChanged()

                        }
                    }

                }


            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


}