package com.app.iagree.adaptor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.renderscript.Sampler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.MainActivity
import com.app.iagree.R
import com.app.iagree.SearchProfileFragment
import com.app.iagree.model.User
import com.app.iagree.ui.dashboard.DashboardFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_search_profile.view.*
import kotlinx.android.synthetic.main.search_user_item.view.*

class SearchUserItemAdaptor(private var mcontext: Context,private var mUser: List<User>,private var isFragment:Boolean= false)
    : RecyclerView.Adapter<SearchUserItemAdaptor.ViewHolder>()

{
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchUserItemAdaptor.ViewHolder {
        val view = LayoutInflater.from(mcontext).inflate(R.layout.search_user_item,parent,false)
        return SearchUserItemAdaptor.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: SearchUserItemAdaptor.ViewHolder, position: Int) {
        val user = mUser[position]

        holder.usernameTextView.text = user.getUsername()
        holder.userFullnameTextView.text = user.getFullname()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.ic_account_circle_black_24dp).into(holder.userProfileImageview)



        if (firebaseUser!!.uid == user!!.getUID()){
            holder.userFollowButton.visibility = View.GONE
        }else{
            holder.userFollowButton.visibility = View.VISIBLE
        }



        holder.itemView.setOnClickListener(View.OnClickListener {
            if (firebaseUser!!.uid == user!!.getUID()){
                    Toast.makeText(mcontext,"Hey! Its your own profile",Toast.LENGTH_LONG).show()
            }else{



                if (isFragment){
                    val pref = mcontext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                    pref.putString("profileID",user.getUID())
                    pref.apply()

                   // Navigation.createNavigateOnClickListener(R.id.action_navigation_search_to_postDetailsFragment)

                    (mcontext as FragmentActivity).supportFragmentManager.beginTransaction()
                        .addToBackStack(null)
                      .add(R.id.nav_host_fragment,SearchProfileFragment()).commit()
                }else{
                    val i  = Intent(mcontext,MainActivity::class.java)
                    i.putExtra("publisherID",user.getUID())
                    mcontext.startActivity(i)
                }
            }
        })


        holder.userFollowButton.setOnClickListener {
            if (holder.userFollowButton.text.toString() == "Follow"){

                firebaseUser?.uid.let { it->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it.toString())
                        .child("Following").child(user.getUID())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful){

                                firebaseUser?.uid.let { it->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful){
                                                holder.userFollowButton.text = "Following"
                                            }
                                        }
                                }

                            }
                        }
                }

                addNotification(user.getUID())

            }
            else if(holder.userFollowButton.text.toString() == "Following"){

                firebaseUser?.uid.let { it->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it.toString())
                        .child("Following").child(user.getUID())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful){

                                firebaseUser?.uid.let { it->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful){
                                                holder.userFollowButton.text = "Request"
                                            }
                                        }
                                }

                            }
                        }
                }

            }
            else if(holder.userFollowButton.text.toString() == "Request"){

                firebaseUser?.uid.let { it->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(user.getUID())
                        .child("Requests").child(it.toString()).child("requestPublisher")
                        .setValue(it.toString()).addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                holder.userFollowButton.text = "Requested"
                            }
                        }
                }

            }
            else if(holder.userFollowButton.text.toString() == "Requested"){

                firebaseUser?.uid.let { it->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(user.getUID())
                        .child("Requests").child(it.toString())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                holder.userFollowButton.text = "Request"
                            }
                        }
                }

            }
        }




        checkFollowingStatus(user.getUID(),holder.userFollowButton)


    }

    class ViewHolder(@NonNull itemView:View): RecyclerView.ViewHolder(itemView){
        var usernameTextView = itemView.findViewById<TextView>(R.id.userName)
        var userProfileImageview = itemView.findViewById<ImageView>(R.id.profileImage)
        var userFullnameTextView = itemView.findViewById<TextView>(R.id.fullName)
        var userFollowButton = itemView.findViewById<Button>(R.id.btnFollow)
    }


    private fun checkFollowingStatus(uid: String, userFollowButton: Button?) {

        val followingRef = firebaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }

        followingRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(uid).exists()){
                    userFollowButton?.text = "Following"
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
                                            userFollowButton!!.text = "Requested"
                                        }else{
                                            userFollowButton?.text = "Request"
                                        }
                                    }

                                    override fun onCancelled(p0: DatabaseError) {

                                    }
                                })
                            }else{
                                userFollowButton?.text = "Follow"
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