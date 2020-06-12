package com.app.iagree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.adaptor.SearchUserItemAdaptor
import com.app.iagree.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.search_fragment.view.*

class ShowUsersWhoLikedPostActivity : AppCompatActivity() {

    private var id : String? = null
    private var title: String? = null

    var userAdaptor : SearchUserItemAdaptor? = null
    var userList: List<User>? = null
    var idList: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_users_who_liked_post)

        val i = intent
        id = i.getStringExtra("id")
        title = i.getStringExtra("title")

        val recyclerView : RecyclerView
        recyclerView = findViewById(R.id.recyclerView_activity_show_users_who_liked_post)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        userList = ArrayList()
        userAdaptor = SearchUserItemAdaptor(this,userList as ArrayList<User>, false)
        recyclerView.adapter = userAdaptor

        idList = ArrayList()
        when(title){
            "Likes"-> getLikes()
            "Followers"-> getFollowers()
            "Following"-> getFollowing()
            "views"-> getViews()
        }

        val t = findViewById<TextView>(R.id.t)
        t.text = title
    }

    private fun getViews() {

        val ref = FirebaseDatabase.getInstance().reference
            .child("Story").child(id!!).child(intent.getStringExtra("storyid")!!)
            .child("views")

        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {


                (idList as ArrayList<String>).clear()
                for (snapshot in p0.children) {
                    (idList as ArrayList<String>).add(snapshot.key!!)
                }
                showUser()

            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })

    }

    private fun getFollowing() {

        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(id!!)
            .child("Following")

        followingRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {


                    (idList as ArrayList<String>).clear()
                    for (snapshot in p0.children) {
                        (idList as ArrayList<String>).add(snapshot.key!!)
                    }
                    showUser()

            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })

    }

    private fun getFollowers() {

        val followerRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(id!!)
            .child("Followers")

        followerRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {


                (idList as ArrayList<String>).clear()
                for (snapshot in p0.children) {
                    (idList as ArrayList<String>).add(snapshot.key!!)
                }
                showUser()

            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })

    }

    private fun getLikes() {
        val LikesRef = FirebaseDatabase.getInstance()
            .reference.child("Likes").child(id!!)

        LikesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    (idList as ArrayList<String>).clear()
                    for (snapshot in p0.children){
                        (idList as ArrayList<String>).add(snapshot.key!!)

                    }

                    showUser()
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun showUser() {

        val userRef = FirebaseDatabase.getInstance().getReference().child("Users")
        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                (userList as ArrayList<User>).clear()

                for(snapshot in p0.children){
                    val user = snapshot.getValue(User::class.java)
                    for (id in idList!!){
                        if (user!!.getUID() == id){
                            (userList as ArrayList<User>).add(user!!)
                        }
                    }
                }

                userAdaptor?.notifyDataSetChanged()

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }

    override fun onBackPressed() {
        finish()
    }
}
