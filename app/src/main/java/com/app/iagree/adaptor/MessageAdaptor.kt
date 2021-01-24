package com.app.iagree.adaptor

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.R
import com.app.iagree.ShowImageActivity
import com.app.iagree.model.message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text
import java.lang.Exception

class MessageAdaptor(mContext:Context,mMessage:MutableList<message>,imageUrl:String)
    :RecyclerView.Adapter<MessageAdaptor.ViewHolder?>()
{

    private var mMessage : MutableList<message>? =null
    private var mContext : Context
    private var firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    private var imageUrl : String ? = null

    init {
        this.mMessage = mMessage
        this.mContext = mContext
        this.imageUrl = imageUrl
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        //left Item
        var ic_profile_message_left : CircleImageView = itemView.findViewById(R.id.ic_profile_message_left)
        var image_message_left : ImageView = itemView.findViewById(R.id.image_message_left)

        //right Item
        var text_message_right : TextView = itemView.findViewById(R.id.text_message_right)
        var text_seen_message_right : TextView = itemView.findViewById(R.id.text_seen_message_right)

        //progressbar
        var progressbar : ProgressBar = itemView.findViewById(R.id.progressbar_image_message)
        var imageSize : TextView = itemView.findViewById(R.id.image_size_messages)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == 0){
            val view =  LayoutInflater.from(mContext).inflate(R.layout.message_left_item,parent,false)
            return ViewHolder(view)
        }else{
            val view =  LayoutInflater.from(mContext).inflate(R.layout.message_right_item,parent,false)
            return ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mMessage!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message : message = mMessage!![position]

        Picasso.get().load(imageUrl).resize(200,200).onlyScaleDown().into(holder.ic_profile_message_left)



        //if message = image
        if (message.getmessage() == "sent you an image" && message.geturl() !=  ""){

            //right image
            if (message.getsender() == firebaseUser!!.uid){




                holder.progressbar!!.visibility = View.VISIBLE
                holder.text_message_right!!.visibility = View.GONE
                holder.image_message_left!!.visibility = View.VISIBLE
                Picasso.get().load(message.geturl()).resize(350,400)
                    .onlyScaleDown().into(holder.image_message_left, object :com.squareup.picasso.Callback{

                        override fun onSuccess() {
                            holder.progressbar!!.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {

                        }
                    })

            }
            //left image
            else if (message.getsender() != firebaseUser!!.uid){
                

                holder.progressbar!!.visibility = View.VISIBLE
                holder.text_message_right!!.visibility = View.GONE
                holder.image_message_left!!.visibility = View.VISIBLE
                Picasso.get().load(message.geturl()).resize(350,400)
                    .onlyScaleDown().into(holder.image_message_left, object :com.squareup.picasso.Callback{
                        override fun onSuccess() {
                            holder.progressbar!!.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {

                        }
                    })

            }

        }else{

                holder.text_message_right!!.text = message.getmessage()

        }

        //seen messages
        if (position == mMessage!!.size-1){

            if (message.getisSeen() == "true"){
                holder.text_seen_message_right!!.text = "seen"
            }else{
                holder.text_seen_message_right!!.text = "sent"
            }


        }
        else{
            holder.text_seen_message_right!!.visibility = View.GONE
        }

        holder.image_message_left.setOnClickListener {
            val i = Intent(mContext,ShowImageActivity::class.java)
            i.putExtra("messageImage",message.geturl())
            mContext.startActivity(i)
        }

        holder.itemView.setOnClickListener {

        }

    }

    private fun deleteMessage(getmessageID: String,sender:String) {
        FirebaseDatabase.getInstance().reference.child("Messages")
                .child(firebaseUser!!.uid).child(sender).child(getmessageID).removeValue()
    }

    override fun getItemViewType(position: Int): Int {

        return if (mMessage!![position].getsender() == (firebaseUser!!.uid)){
            1
        }else {
            0
        }
    }

    override fun getItemId(position: Int): Long {
        return if (mMessage!![position].getsender() == (firebaseUser!!.uid)){
            1
        }else{
            0
        }
    }

}