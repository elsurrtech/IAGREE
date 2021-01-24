package com.app.iagree.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.R
import com.app.iagree.model.User
import com.app.iagree.model.leaderboard
import com.flaviofaria.kenburnsview.KenBurnsView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class leaderboardAdaptor (private val mContext: Context, mList:MutableList<leaderboard>)
    : RecyclerView.Adapter<leaderboardAdaptor.ViewHolder?>() {

    private var mList: MutableList<leaderboard>? = null
    private var firebaseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser


    init {
        this.mList = mList
    }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var usernameleaderboard: TextView = itemView.findViewById(R.id.username)
        val fullName : TextView = itemView.findViewById(R.id.fullName)
        val imageUrl : KenBurnsView = itemView.findViewById(R.id.kbv)
        val circleImageView:CircleImageView = itemView.findViewById(R.id.circleImage_leaderboard)
        val btnFollow:Button = itemView.findViewById(R.id.btnFollow_leaderboard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =  LayoutInflater.from(mContext).inflate(R.layout.leaderboard_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        return mList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val y: leaderboard = mList!![position]
        checkFollowingStatus(y.getuid(),holder.btnFollow)

        holder.btnFollow.setOnClickListener {
            if (holder.btnFollow.text.toString() == "Follow"){

                firebaseUser?.uid.let { it->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it.toString())
                        .child("Following").child(y.getuid())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful){

                                firebaseUser?.uid.let { it->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(y.getuid())
                                        .child("Followers").child(it.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful){
                                                holder.btnFollow.text = "Following"
                                            }
                                        }
                                }

                            }
                        }
                }

                addNotification(y.getuid())

            }
            else if(holder.btnFollow.text.toString() == "Following"){

                firebaseUser?.uid.let { it->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it.toString())
                        .child("Following").child(y.getuid())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful){

                                firebaseUser?.uid.let { it->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(y.getuid())
                                        .child("Followers").child(it.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful){
                                                holder.btnFollow.text = "Request"
                                            }
                                        }
                                }

                            }
                        }
                }

            }
            else if(holder.btnFollow.text.toString() == "Request"){

                firebaseUser?.uid.let { it->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(y.getuid())
                        .child("Requests").child(it.toString()).child("requestPublisher")
                        .setValue(it.toString()).addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                holder.btnFollow.text = "Requested"
                            }
                        }
                }

            }
            else if(holder.btnFollow.text.toString() == "Requested"){

                firebaseUser?.uid.let { it->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(y.getuid())
                        .child("Requests").child(it.toString())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                holder.btnFollow.text = "Request"
                            }
                        }
                }

            }
        }

        userInfo(holder.imageUrl,holder.usernameleaderboard,holder.fullName,y.getuid(),holder.circleImageView)

    }

    private fun userInfo(imageView: KenBurnsView, username:TextView, fullName: TextView, publisherID:String,circleImage:CircleImageView){
        val usersRef = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(publisherID)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).into(imageView)
                    username.text = user!!.getUsername()
                    fullName.text = user!!.getFullname()
                    Picasso.get().load(user!!.getImage()).into(circleImage)
                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })
    }

    private fun checkFollowingStatus(uid: String, btnFollow: Button?) {

        val followingRef = firebaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }

        followingRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(uid).exists()){
                    btnFollow?.text = "Following"
                }
                else{
                    val ref  = FirebaseDatabase.getInstance().reference.child("PrivateAccounts")
                    ref.addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            if (dataSnapshot.child(uid).exists()){
                                val requestedRef = FirebaseDatabase.getInstance().reference
                                    .child("Follow").child(uid)
                                    .child("Requests").child(firebaseUser!!.uid)
                                requestedRef.addValueEventListener(object :ValueEventListener{
                                    override fun onDataChange(goti: DataSnapshot) {
                                        if (goti.exists()){
                                            btnFollow!!.text = "Requested"
                                        }else{
                                            btnFollow?.text = "Request"
                                        }
                                    }

                                    override fun onCancelled(p0: DatabaseError) {

                                    }
                                })
                            }else{
                                btnFollow?.text = "Follow"
                            }

                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }
                    })

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }

    private fun addNotification(userID:String){
        val notiref = FirebaseDatabase.getInstance().reference
            .child("Notifications").child(userID)

        val notiMap = HashMap<String, Any?>()
        notiMap["userID"] = firebaseUser!!.uid
        notiMap["text"] = "started following you"
        notiMap["postID"] = ""
        notiMap["isPost"] = false

        notiref.push().setValue(notiMap)

    }
}