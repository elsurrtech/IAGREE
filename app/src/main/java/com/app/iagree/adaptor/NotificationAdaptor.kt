package com.app.iagree.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.PostDetailsFragment
import com.app.iagree.R
import com.app.iagree.SearchProfileFragment
import com.app.iagree.model.Post
import com.app.iagree.model.User
import com.app.iagree.model.notification
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

class NotificationAdaptor(private val mContext: Context, mNotification:List<notification>)
    :RecyclerView.Adapter<NotificationAdaptor.ViewHolder?>()

    {

        private var mNotification: List<notification>? = null

        init {
                this.mNotification = mNotification
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): NotificationAdaptor.ViewHolder {

            val view =  LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false)
            return ViewHolder(view)

        }

        override fun getItemCount(): Int {

            return mNotification!!.size

        }

        override fun onBindViewHolder(holder: NotificationAdaptor.ViewHolder, position: Int) {

            val notification: notification = mNotification!![position]

            if (notification.gettext().equals("started following you")){
                holder.notificationNotification.text = "started following you."
            }else if (notification.gettext().equals("liked your post")){
                holder.notificationNotification.text = "liked your post."
            }else if(notification.gettext().contains("commented on your photo")){
                holder.notificationNotification.text = notification.gettext().replace("commented on your photo","commented on your photo: ")
            }else{
                holder.notificationNotification.text = notification.gettext()
            }

            userInfo(holder.userImageNotification,holder.usernameNotification,notification.getuserID())

            if (notification.isPost()){
                holder.postImageNotification.visibility = View.VISIBLE
                getPostImage(holder.postImageNotification,notification.getpostID())
            }else{
                holder.postImageNotification.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                if (notification.isPost()){
                    val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                    editor.putString("postID",notification.getpostID())
                    editor.apply()
                    (mContext as FragmentActivity).supportFragmentManager.beginTransaction().addToBackStack(null)
                        .replace(R.id.nav_host_fragment,
                        PostDetailsFragment()
                    ).commit()

                }else{
                    val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                    editor.putString("profileID",notification.getuserID())
                    editor.apply()
                    (mContext as FragmentActivity).supportFragmentManager
                        .beginTransaction().addToBackStack(null)
                        .replace(R.id.nav_host_fragment, SearchProfileFragment()
                    ).commit()
                }
            }

        }

        inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
            var userImageNotification: CircleImageView = itemView.findViewById(R.id.image_notification_item)
            var usernameNotification : TextView = itemView.findViewById(R.id.userName_notification_item)
            var notificationNotification : TextView = itemView.findViewById(R.id.notification_notification_item)
            var postImageNotification : ImageView = itemView.findViewById(R.id.post_image_notification_item)
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

        private fun getPostImage(imageView: ImageView,postID:String){
            val usersRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postID)
            usersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {

                    if (p0.exists()){
                        val post = p0.getValue<Post>(Post::class.java)

                        Picasso.get().load(post!!.getPostImage()).placeholder(R.drawable.ic_account_circle_black_24dp).into(imageView)

                    }
                }

                override fun onCancelled(p0: DatabaseError) {


                }
            })
        }

    }