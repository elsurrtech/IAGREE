package com.app.iagree.adaptor

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.R
import com.app.iagree.messages.ChatActivity
import com.app.iagree.model.MessageList
import com.app.iagree.model.User
import com.app.iagree.model.confessions
import com.borjabravo.readmoretextview.ReadMoreTextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MessageFragmentItemAdaptor(private var mContext:Context,mMessageList: MutableList<MessageList>?):
    RecyclerView.Adapter<MessageFragmentItemAdaptor.ViewHolder>()
{

    private var mMessageList : MutableList<MessageList>? = null

    init {
        this.mMessageList = mMessageList
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        val image  = itemView.findViewById<CircleImageView>(R.id.image_message_fragment)
        val username : TextView =  itemView.findViewById(R.id.username_message_fragment)
        val message : TextView = itemView.findViewById(R.id.message_message_fragment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =  LayoutInflater.from(mContext).inflate(R.layout.message_fragment_item,parent,false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return mMessageList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val messageList = mMessageList!![position]
        userInfo(holder.image,holder.username,messageList.getId())
        holder.itemView.setOnClickListener {
            val i = Intent(mContext, ChatActivity::class.java)
            i.putExtra("receiverID",messageList.getId())
            mContext.startActivity(i)
        }
    }

    private fun userInfo(imageView: ImageView, username:TextView, publisherID:String){
        val usersRef = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(publisherID)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).resize(200,200).onlyScaleDown().into(imageView)
                    username.text = user!!.getUsername()

                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })
    }

}