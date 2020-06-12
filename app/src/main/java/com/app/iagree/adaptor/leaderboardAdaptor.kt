package com.app.iagree.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.R
import com.app.iagree.model.User
import com.app.iagree.model.leaderboard
import com.flaviofaria.kenburnsview.KenBurnsView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class leaderboardAdaptor (private val mContext: Context, mList:MutableList<leaderboard>)
    : RecyclerView.Adapter<leaderboardAdaptor.ViewHolder?>() {

    private var mList: MutableList<leaderboard>? = null


    init {
        this.mList = mList
    }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var usernameleaderboard: TextView = itemView.findViewById(R.id.username)
        val fullName : TextView = itemView.findViewById(R.id.fullName)
        val imageUrl : KenBurnsView = itemView.findViewById(R.id.kbv)
        val circleImageView:CircleImageView = itemView.findViewById(R.id.circleImage_leaderboard)
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
}