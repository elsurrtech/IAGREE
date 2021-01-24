package com.app.iagree.adaptor

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.app.iagree.model.User
import com.app.iagree.model.confessions
import com.borjabravo.readmoretextview.ReadMoreTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ConfessionsAdaptor(private val mContext: Context,private val keyList: MutableList<String>?, mConfession:MutableList<confessions>?):
    RecyclerView.Adapter<ConfessionsAdaptor.ViewHolder>()  {

    private var mConfession : MutableList<confessions>? = null

    init {
        this.mConfession = mConfession
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var btnStartConfession = itemView.findViewById<Button>(R.id.start_confession)
        var btnIgnoreConfession = itemView.findViewById<Button>(R.id.ignore_confession)
        var message: ReadMoreTextView = itemView.findViewById(R.id.message_confession_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfessionsAdaptor.ViewHolder {
        val view =  LayoutInflater.from(mContext).inflate(R.layout.confession_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mConfession!!.size
    }

    override fun onBindViewHolder(holder: ConfessionsAdaptor.ViewHolder, position: Int) {
        val confession = mConfession!![position]
        holder!!.message.text = confession.getMessage()

        holder.btnStartConfession.setOnClickListener {
            val i = Intent(mContext,ChatActivity::class.java)
            i.putExtra("receiverID",confession.getPublisher())
            mContext.startActivity(i)
        }
        holder.btnIgnoreConfession.setOnClickListener {
            keyList?.get(position!!)?.let { it -> deleteConfession(it) }
        }
    }

    private fun deleteConfession(confessions: String){
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("ConfessionRequests").child(confessions).removeValue()
    }


}