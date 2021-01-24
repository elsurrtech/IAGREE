package com.app.iagree

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.adaptor.SuggestionAdaptor
import com.app.iagree.adaptor.leaderboardAdaptor
import com.app.iagree.model.leaderboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_suggestions.*

class SuggestionsActivity : AppCompatActivity() {

    private var leaderboardAdaptor: SuggestionAdaptor? = null
    private var leaderBoardList: MutableList<leaderboard>? = null

    private lateinit var profileID: String
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestions)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        profileID = firebaseUser.uid

        val recyclerView : RecyclerView = findViewById(R.id.recyclerview_suggestions)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        leaderBoardList = ArrayList()
        leaderboardAdaptor = SuggestionAdaptor(this,leaderBoardList)
        recyclerView.adapter = leaderboardAdaptor

        r3_suggestions?.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        readSuggestions()
        checkTotalFollowing()
    }

    private fun readSuggestions(){
        val ref = FirebaseDatabase.getInstance().reference.child("leaderBoard")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){

                    leaderBoardList!!.clear()

                    for(snapshot in p0.children){
                        val x = snapshot.getValue(leaderboard::class.java)
                        leaderBoardList!!.add(x!!)
                    }

                    leaderboardAdaptor!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun checkTotalFollowing(){
        val ref = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(profileID).child("Following")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    if (p0.childrenCount >= 3){
                        r3_suggestions!!.visibility = View.VISIBLE
                        r3_suggestions!!.startAnimation(AnimationUtils.loadAnimation(this@SuggestionsActivity,R.anim.animdetailsentryup))
                    }
                    if (p0.childrenCount < 3){
                        if (r3_suggestions!!.visibility == View.VISIBLE){

                            r3_suggestions!!.startAnimation(AnimationUtils.loadAnimation(this@SuggestionsActivity,R.anim.animdetailsexit))
                            r3_suggestions?.visibility = View.GONE

                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}