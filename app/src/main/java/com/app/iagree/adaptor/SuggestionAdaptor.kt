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
import kotlinx.android.synthetic.main.fragment_search_profile.*

class SuggestionAdaptor(private val mContext: Context, mList:MutableList<leaderboard>?)
    : RecyclerView.Adapter<SuggestionAdaptor.ViewHolder>()
{

    private var mList: MutableList<leaderboard>? = null
    private var firebaseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser


    init {
        this.mList = mList
    }

    class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
      var userName = itemView.findViewById<TextView>(R.id.username_suggestion)
        var fullname = itemView.findViewById<TextView>(R.id.fullname_suggestion)
        var university  = itemView.findViewById<TextView>(R.id.university_suggestion)
        var profileImage = itemView.findViewById<CircleImageView>(R.id.image_suggestions)
        var degree = itemView.findViewById<TextView>(R.id.programme_suggestion)
        var btnFollow = itemView.findViewById<Button>(R.id.btnfollow_suggestion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =  LayoutInflater.from(mContext).inflate(R.layout.item_suggestion,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val y: leaderboard = mList!![position]
        
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val profileID = y.getuid()

        checkFollowingStatus(profileID,holder.btnFollow)

        holder.btnFollow.setOnClickListener {
            if (holder.btnFollow.text.toString() == "Follow"){

                firebaseUser?.uid.let { it->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it.toString())
                        .child("Following").child(profileID)
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful){

                                firebaseUser?.uid.let { it->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(profileID)
                                        .child("Followers").child(it.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful){

                                            }
                                        }
                                }

                            }
                        }
                }

                addNotification(profileID)

            }
            else if(holder.btnFollow.text.toString() == "Following"){

                firebaseUser?.uid.let { it->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it.toString())
                        .child("Following").child(profileID)
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful){

                                firebaseUser?.uid.let { it->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(profileID)
                                        .child("Followers").child(it.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful){

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
                        .child("Follow").child(profileID)
                        .child("Requests").child(it.toString()).child("requestPublisher")
                        .setValue(it.toString()).addOnCompleteListener { task ->
                            if (task.isSuccessful){

                            }
                        }
                }

            }
            else if(holder.btnFollow.text.toString() == "Requested"){

                firebaseUser?.uid.let { it->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(profileID)
                        .child("Requests").child(it.toString())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful){

                            }
                        }
                }

            }
        }

        userInfo(y.getuid(),holder.profileImage,holder.userName,holder.fullname,holder.university,holder.degree)
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

    private fun userInfo(publisherID:String,imageView: CircleImageView, username:TextView, fullName: TextView, university:TextView,degree:TextView){
        val usersRef = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(publisherID)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).into(imageView)
                    username.text = "@"+user!!.getUsername()
                    fullName.text = user!!.getFullname()
                    university.text = user.getCollege()
                    degree.text= user.getDegree()+", "+ user.getProgramme()
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