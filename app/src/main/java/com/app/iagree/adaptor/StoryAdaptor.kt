package com.app.iagree.adaptor

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.AddStoryActivity
import com.app.iagree.MainActivity
import com.app.iagree.R
import com.app.iagree.StoryActivity
import com.app.iagree.model.Story
import com.app.iagree.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jgabrielfreitas.core.BlurImageView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

class StoryAdaptor(private val mContext:Context,private val mStory: List<Story>): RecyclerView.Adapter<StoryAdaptor.ViewHolder>()
{
    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){

        //story Item
        var story_image_seen: CircleImageView? = null
        var story_image: CircleImageView? = null
        var story_username: TextView? = null

        //Add Story Item
        var story_plus_btn: CircleImageView? = null
        var add_story_text: TextView? = null

        init {
            //story item
            story_image_seen = itemView.findViewById(R.id.story_image_seen)
            story_image = itemView.findViewById(R.id.story_image)
            story_username = itemView.findViewById(R.id.story_username)

            //add story item
            story_plus_btn = itemView.findViewById(R.id.story_add)
            add_story_text = itemView.findViewById(R.id.add_story_text)
        }


    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0){
            return 0
        }
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return if (viewType == 0){
           val view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item,parent,false)
           ViewHolder(view)
       }else{
           val view = LayoutInflater.from(mContext).inflate(R.layout.story_item,parent,false)
           ViewHolder(view)
       }
    }

    override fun getItemCount(): Int {
       return mStory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = mStory[position]

        userInfo(holder,story.getUserID(),position)

        if (holder.adapterPosition !== 0){
            seenStory(holder,story.getUserID())
        }
        if (holder.adapterPosition === 0){
            myStories(holder.add_story_text!!,holder.story_plus_btn!!,false)
        }

        holder.itemView.setOnClickListener {
            if (holder.adapterPosition === 0){
                myStories(holder.add_story_text!!,holder.story_plus_btn!!,true)
            } else{
                val i  = Intent(mContext, StoryActivity::class.java)
                i.putExtra("userid",story.getUserID())
                mContext.startActivity(i)
            }

        }

    }

    private fun userInfo(viewHolder: ViewHolder,userid:String,position: Int){
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userid)
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.ic_account_circle_black_24dp).into(viewHolder.story_image)
                    if (position!=0){
                        Picasso.get().load(user!!.getImage()).into(viewHolder.story_image_seen)
                        viewHolder.story_username!!.text=user.getUsername()
                    }

                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })
    }

    private fun seenStory(viewHolder: ViewHolder, userid: String){
        val storyRef = FirebaseDatabase.getInstance().reference.child("Story").child(userid)

        storyRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var i = 0
                for(snapshot in p0.children  ){
                    if (!snapshot.child("views").child(FirebaseAuth.getInstance()
                            .currentUser!!.uid).exists() && System.currentTimeMillis() < snapshot.getValue(Story::class.java)!!.getTimeEnd())
                    {
                        i++
                    }
                }
                if (i>0){
                    viewHolder.story_image!!.visibility = View.VISIBLE
                    viewHolder.story_image_seen!!.visibility = View.GONE
                }else{
                    viewHolder.story_image!!.visibility = View.GONE
                    viewHolder.story_image_seen!!.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun myStories(textView:TextView, imageView: ImageView, click: Boolean){
        val storyRef = FirebaseDatabase.getInstance().reference.child("Story")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        storyRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                var counter = 0
                    val timeCurrent=System.currentTimeMillis()

                    for (snapshot in p0.children){

                        val story=snapshot.getValue(Story::class.java)

                        if (timeCurrent>story!!.getTimeStart() && timeCurrent<story!!.getTimeEnd())
                        {
                            counter++
                        }
                    }

                if (click){
                    if (counter>0){
                        val alertDialog = AlertDialog.Builder(mContext).create()
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "View Story"){
                                dialog: DialogInterface?, which: Int ->

                            val i = Intent(mContext, StoryActivity::class.java)
                            i.putExtra("userid",FirebaseAuth.getInstance().currentUser!!.uid)
                            mContext.startActivity(i)
                            dialog!!.dismiss()
                        }

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Add Story"){
                                dialog: DialogInterface?, which: Int ->

                            val i = Intent(mContext, AddStoryActivity::class.java)
                            i.putExtra("userid",FirebaseAuth.getInstance().currentUser!!.uid)
                            mContext.startActivity(i)
                            dialog!!.dismiss()
                        }

                        alertDialog.show()
                    }
                    else{
                        val i = Intent(mContext, AddStoryActivity::class.java)
                        i.putExtra("userid",FirebaseAuth.getInstance().currentUser!!.uid)
                        mContext.startActivity(i)
                    }
                }
                else{
                    if (counter>0){
                        textView.text = "My Story"
                        imageView.visibility =  View.GONE
                    }else{
                        textView.text = "Add Story"
                        imageView.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}