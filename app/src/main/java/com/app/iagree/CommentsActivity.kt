package com.app.iagree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.adaptor.CommentsAdaptor
import com.app.iagree.model.Comment
import com.app.iagree.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CommentsActivity : AppCompatActivity() {

    public var postID: String? = ""
    private var publisherID: String? = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentsAdaptor: CommentsAdaptor? = null
    private var commentList: MutableList<Comment>? = null
    private var commentPublishedList: MutableList<Comment>? = null

    private var keyList: MutableList<String>? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val intent = intent
        postID = intent.getStringExtra("postID")
        publisherID = intent.getStringExtra("publisherID")
        firebaseUser = FirebaseAuth.getInstance().currentUser

        getPostImage()
        readComments()

        send_image_comment.setOnClickListener(View.OnClickListener {
            if (et_activity_comment!!.text.toString() == ""){
                Toast.makeText(this,"Enter a comment", Toast.LENGTH_SHORT).show()
            }else{
                addComment()
            }
        })

        val recyclerView: RecyclerView
        recyclerView = findViewById(R.id.recyclerView_activity_comments)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager

        commentList =ArrayList()
        keyList = ArrayList()
        commentsAdaptor = CommentsAdaptor(postID!!,this,keyList,commentList)
        recyclerView.adapter = commentsAdaptor
    }

    private fun addComment() {
        val commentref = FirebaseDatabase.getInstance().reference.child("Comments").child(postID!!)
        val commentMap = HashMap<String, Any>()
        commentMap["comment"] = et_activity_comment.text.toString()
        commentMap["publisher"] = firebaseUser!!.uid
        commentref.push().setValue(commentMap)

        addNotification()

        et_activity_comment!!.text.clear()
    }

    private fun getPostImage(){
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser!!.uid)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.ic_account_circle_black_24dp).into(profile_image_comment)

                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })
    }
    private fun readComments(){
        val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments").child(postID!!)
        commentsRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){

                    relativeLayout_activity_comments.visibility =View.GONE
                    commentList!!.clear()
                    keyList!!.clear()
                    for (snapshot in p0.children){
                        val c = snapshot.getValue(Comment::class.java)
                        commentList!!.add(c!!)
                        val s= snapshot.key
                        keyList!!.add(s!!)
                    }

                    commentList!!.reverse()
                    keyList!!.reverse()

                }else{
                    relativeLayout_activity_comments.visibility = View.VISIBLE
                }

                commentsAdaptor!!.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun addNotification(){
        val notiref = FirebaseDatabase.getInstance().reference
            .child("Notifications").child(publisherID!!)

        val notiMap = HashMap<String, Any?>()
        notiMap["userID"] = firebaseUser!!.uid
        notiMap["text"] = "commented on your photo"+ et_activity_comment!!.text.toString()
        notiMap["postID"] = postID
        notiMap["isPost"] = true

        notiref.push().setValue(notiMap)

    }
}
