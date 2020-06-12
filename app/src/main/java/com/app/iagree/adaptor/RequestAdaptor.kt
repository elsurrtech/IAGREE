package com.app.iagree.adaptor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.R
import com.app.iagree.SearchProfileFragment
import com.app.iagree.model.User
import com.app.iagree.model.notification
import com.app.iagree.model.requests
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class RequestAdaptor(private val mContext:Context, mRequest:MutableList<requests>?):RecyclerView.Adapter<RequestAdaptor.ViewHolder>() {

    private var mRequest: MutableList<requests>? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    init {
        this.mRequest = mRequest
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var textUsername: TextView = itemView.findViewById(R.id.username_request_item)
        var imageUser : CircleImageView = itemView.findViewById(R.id.userImage_request_item)
        var btnIgnore : Button = itemView.findViewById(R.id.btnIgnore)
        var btnAccept : Button = itemView.findViewById(R.id.btnAccept)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =  LayoutInflater.from(mContext).inflate(R.layout.request_item,parent,false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return mRequest!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = mRequest!![position]
        userInfo(holder.imageUser,holder.textUsername, request.getrequestPublisher())

        holder.btnAccept.setOnClickListener {

            firebaseUser?.uid.let { it->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(it.toString())
                    .child("Followers").child(request.getrequestPublisher())
                    .setValue(true).addOnCompleteListener { task ->
                        if (task.isSuccessful){

                            firebaseUser?.uid.let { it->
                                FirebaseDatabase.getInstance().reference
                                    .child("Follow").child(request.getrequestPublisher())
                                    .child("Following").child(it.toString())
                                    .setValue(true)
                            }
                            FirebaseDatabase.getInstance().reference
                                .child("Follow").child(firebaseUser!!.uid)
                                .child("Requests").child(request.getrequestPublisher()).removeValue()

                            Toast.makeText(mContext,"@"+holder.textUsername.text+": Accepted",Toast.LENGTH_SHORT).show()

                        }
                    }
            }

        }

        holder.btnIgnore.setOnClickListener {

            FirebaseDatabase.getInstance().reference
                .child("Follow").child(firebaseUser!!.uid)
                .child("Requests").child(request.getrequestPublisher()).removeValue()

        }

        holder.textUsername.setOnClickListener {
            val pref = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            pref.putString("profileID",request.getrequestPublisher())
            pref.apply()

            val fragment = SearchProfileFragment()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment).commit()
        }

    }

    private fun userInfo(imageView: ImageView,username:TextView,publisherID:String){
        val usersRef = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(publisherID)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).into(imageView)
                    username.text = user!!.getUsername()

                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })
    }

}