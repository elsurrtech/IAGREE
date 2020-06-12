package com.app.iagree.adaptor

import android.content.Context
import android.renderscript.Sampler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.CommentsActivity
import com.app.iagree.R
import com.app.iagree.model.Comment
import com.app.iagree.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentsAdaptor (private var postID: String,private val mContext: Context,private val keyList: MutableList<String>?,
                       private val mComment: MutableList<Comment>?) : RecyclerView.Adapter<CommentsAdaptor.ViewHolder>() {

    private var firebaseUser:FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdaptor.ViewHolder {
        val view =  LayoutInflater.from(mContext).inflate(R.layout.comment_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mComment!!.size
    }

    override fun onBindViewHolder(holder: CommentsAdaptor.ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        holder.icon_verified.visibility = View.GONE
        holder.more.visibility = View.GONE

        val comment = mComment!![position]
        holder.comment.text = comment.getComment()
        getUserInfo(holder.username,holder.imageProfile,comment.getPublisher())

        if (comment.getPublisher() == firebaseUser!!.uid){
            holder.more.visibility = View.VISIBLE
        }


       holder.more.setOnClickListener {
           val popupMenu = PopupMenu(mContext,it)
           popupMenu.menuInflater.inflate(R.menu.post_delete,popupMenu.menu)
           popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
               override fun onMenuItemClick(item: MenuItem?): Boolean {
                   when(item!!.itemId){
                       R.id.delete-> keyList?.get(position!!)?.let { it1 -> deleteComment(it1) }

                   }

                   return true
               }
           })
           popupMenu.show()
       }

        checkVerifiedAccount(holder.icon_verified,comment.getPublisher())
    }

    private fun getUserInfo(username: TextView, imageProfile: ImageView, publisher: String) {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisher)

        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val user = p0.getValue(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.ic_account_circle_black_24dp).into(imageProfile)
                    username.text = user!!.getUsername()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var imageProfile: CircleImageView = itemView.findViewById(R.id.image_publisher_comment_layout)
        var username : TextView = itemView.findViewById(R.id.username_publisher_comment_layout)
        var comment: TextView = itemView.findViewById(R.id.comments_text_publisher_comment_layout)
        val icon_verified = itemView.findViewById<ImageView>(R.id.image_verified_comment)
        val more  = itemView.findViewById<ImageView>(R.id.more_comment)
    }

    private fun checkVerifiedAccount(image: ImageView,profileID:String){
        val ref = FirebaseDatabase.getInstance().reference.child("verified").child(profileID)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    image.visibility = View.VISIBLE
                }else{
                    image.visibility = View.GONE
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun deleteComment(comment:String){
        val ref = FirebaseDatabase.getInstance().reference
            ref.child("Comments").child(postID).child(comment).removeValue()
    }


}