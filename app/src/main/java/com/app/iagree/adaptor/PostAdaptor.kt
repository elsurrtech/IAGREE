package com.app.iagree.adaptor

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.animate
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.agrawalsuneet.dotsloader.loaders.LazyLoader
import com.app.iagree.*
import com.app.iagree.model.Post
import com.app.iagree.model.User
import com.app.iagree.ui.homepages.HomeFragmentContainer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import xyz.hanks.library.bang.CircleView
import xyz.hanks.library.bang.SmallBangView
import java.lang.Exception

class PostAdaptor
    (private val mContext: Context, private val mPost: List<Post>): RecyclerView.Adapter<PostAdaptor.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var profileImg: CircleImageView = itemView.findViewById(R.id.image_publisher_post_layout)
        var postImage: ImageView = itemView.findViewById(R.id.post_image_post_layout)
        var likeButton: SmallBangView = itemView.findViewById(R.id.like_heart)
        var commentButton: ImageView = itemView.findViewById(R.id.image_comment_post_layout)
        var saveButton: SmallBangView = itemView.findViewById(R.id.save_smallBang)
        var userName: TextView = itemView.findViewById(R.id.username_image_publisher_post_layout)
        var likes: TextView = itemView.findViewById(R.id.text_like_post_layout)
        var publisher: TextView = itemView.findViewById(R.id.text_publisher_post_layout)
        var description: TextView = itemView.findViewById(R.id.text_description_post_layout)
        var comment: TextView = itemView.findViewById(R.id.text_comment_post_layout)
        var more : ImageView = itemView.findViewById(R.id.more)
        var postLayout: LinearLayout = itemView.findViewById(R.id.post_layout)
        var loader_layout: LinearLayout = itemView.findViewById(R.id.loader_post_layout)
        var t2 : TextView = itemView.findViewById(R.id.t2_postLayout)
        val icon_verified = itemView.findViewById<ImageView>(R.id.image_verified_post)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        return mPost.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]

        holder.icon_verified.visibility = View.GONE

        //verified Account
        checkVerifiedAccount(holder.icon_verified,post.getPublisher())


        //loader
        val loader = LazyLoader(mContext,15,50, ContextCompat.getColor(mContext, R.color.white),
            ContextCompat.getColor(mContext, R.color.white),
            ContextCompat.getColor(mContext, R.color.white)).apply {
            animDuration = 1500
            firstDelayDuration = 100
            secondDelayDuration = 200
            interpolator = DecelerateInterpolator()
        }

        holder.loader_layout.addView(loader)
        holder.postLayout.visibility = View.GONE


        Picasso.get().load(post.getPostImage()).into(holder.postImage, object : com.squareup.picasso.Callback{
            override fun onSuccess() {
                holder.postLayout.visibility =View.VISIBLE
                holder.loader_layout.removeView(loader)
            }

            override fun onError(e: Exception?) {

            }
        })


        if (post.getDescription().equals("")){
            holder.description.visibility = View.GONE
        }else{
            holder.description.visibility = View.VISIBLE
            holder.description.text = post.getDescription()
        }

        //
       

        holder.userName.setOnClickListener {

            if (firebaseUser!!.uid == post.getPublisher()){
                Toast.makeText(mContext,"Hey! its your own Profile",Toast.LENGTH_SHORT).show()
            }else{
                val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                editor.putString("profileID",post.getPublisher())
                editor.apply()
                (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.nav_host_fragment, SearchProfileFragment())
                    .hide(HomeFragmentContainer()).commit()
            }



        }

        holder.profileImg.setOnClickListener {
            if (firebaseUser!!.uid == post.getPublisher()){
                Toast.makeText(mContext,"Hey! its your own Profile",Toast.LENGTH_SHORT).show()
            }else{
                val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                editor.putString("profileID",post.getPublisher())
                editor.apply()
                (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.nav_host_fragment, SearchProfileFragment()
                    ).commit()
            }

        }

        publisherInfo(holder.profileImg, holder.userName,holder.t2, holder.publisher, post.getPublisher())
        isLikes(post.getPostID(),holder.likeButton)
        numberOfLikes(holder.likes,post.getPostID())
        numberOfComments(holder.comment,post.getPostID())
        checkSavedStatus(post.getPostID(),holder.saveButton)

        //like button
        holder.likeButton.setOnClickListener {
            if (holder.likeButton.isSelected){
                holder.likeButton.isSelected = false

                FirebaseDatabase.getInstance().reference.child("Likes").child(post.getPostID())
                    .child(firebaseUser!!.uid).removeValue()

            }else{
                holder.likeButton.isSelected = true
                holder.likeButton.likeAnimation(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {

                    }
                })

                FirebaseDatabase.getInstance().reference.child("Likes").child(post.getPostID())
                    .child(firebaseUser!!.uid).setValue(true)
                addNotification(post.getPublisher(),post.getPostID())



            }
        }

        //comment
        holder.commentButton.setOnClickListener {

            val i = Intent(mContext,CommentsActivity::class.java)
            i.putExtra("postID",post.getPostID())
            i.putExtra("publisherID",post.getPublisher())
            mContext.startActivity(i)
        }

        holder.comment.setOnClickListener {
            val i = Intent(mContext,CommentsActivity::class.java)
            i.putExtra("postID",post.getPostID())
            i.putExtra("publisherID",post.getPublisher())
            mContext.startActivity(i)
        }

        //save
        holder.saveButton.setOnClickListener {
            if (holder.saveButton.isSelected){
                holder.saveButton.isSelected = false
                FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid).child(post.getPostID())
                    .removeValue()


            }else{
                holder.saveButton.isSelected = true
                holder.saveButton.likeAnimation(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {

                    }
                })
                FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid).child(post.getPostID())
                    .setValue(true)
            }
        }

        holder.likes.setOnClickListener {
            val i = Intent(mContext,ShowUsersWhoLikedPostActivity::class.java)
            i.putExtra("id",post.getPostID())
            i.putExtra("title","Likes")
            mContext.startActivity(i)
        }

        holder.more.setOnClickListener {

            if (firebaseUser!!.uid == post.getPublisher()){

                val popupMenu = PopupMenu(mContext,it)
                popupMenu.menuInflater.inflate(R.menu.post_delete,popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
                    override fun onMenuItemClick(item: MenuItem?): Boolean {

                        when(item!!.itemId){
                            R.id.delete-> deletePost(post.getPostID())
                        }

                        return true
                    }
                })
                popupMenu.show()

            }else{
                val popupMenu = PopupMenu(mContext,it)
                popupMenu.menuInflater.inflate(R.menu.post_report,popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
                    override fun onMenuItemClick(item: MenuItem?): Boolean {

                        when(item!!.itemId){
                            R.id.report-> reportPost(firebaseUser!!.uid,post.getPostID())
                        }

                        return true
                    }
                })
                popupMenu.show()
            }

        }
    }

    private fun numberOfLikes(likes: TextView, postID: String) {
        val LikesRef = FirebaseDatabase.getInstance()
            .reference.child("Likes").child(postID)

         LikesRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    if (p0.childrenCount.toString() == "1"){
                        likes.text = p0.childrenCount.toString()+" Like"
                    }else{
                        likes.text =p0.childrenCount.toString()+" Likes"
                    }

                }
                else{
                    likes.text ="0 Likes"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun numberOfComments(comments: TextView, postID: String) {
        val commentsRef = FirebaseDatabase.getInstance()
            .reference.child("Comments").child(postID)

        commentsRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    if (p0.childrenCount.toString() == "1"){
                        comments.text ="view "+p0.childrenCount.toString() + " Comment"
                    }else{
                        comments.text ="view all "+p0.childrenCount.toString() + " Comments"
                    }
                }else{
                    comments.text = "be the first to comment here"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    private fun isLikes(postID: String, likeButton: SmallBangView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val LikesRef = FirebaseDatabase.getInstance()
            .reference.child("Likes").child(postID)

        LikesRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(firebaseUser!!.uid).exists()){
                    likeButton.isSelected = true
                    likeButton.tag = "Liked"
                }else{
                    likeButton.tag = "Like"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, t2:TextView, publisher: TextView, publisherID: String) {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){

                    val user = p0.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.ic_account_circle_black_24dp).into(profileImage)
                    userName.text = user!!.getUsername()
                    publisher.text = user!!.getFullname()
                    t2.text = user!!.getUsername()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun checkSavedStatus(postID:String, imageView: SmallBangView){
        val savesRef = FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid)
        savesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(postID).exists()){
                    imageView.isSelected = true
                    imageView.tag = "saved"
                }else{

                    imageView.tag = "save"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun addNotification(userID:String, postID:String){
        val notiref = FirebaseDatabase.getInstance().reference
            .child("Notifications").child(userID)

        val notiMap = HashMap<String, Any?>()
        notiMap["userID"] = firebaseUser!!.uid
        notiMap["text"] = "liked your post"
        notiMap["postID"] = postID
        notiMap["isPost"] = true

        notiref.push().setValue(notiMap)

    }

    private fun reportPost(reporter: String,postID: String){
        val reportRef = FirebaseDatabase.getInstance().reference.child("Reports").child(postID)

        val reportMap = HashMap<String, Any?>()
        reportMap["reporter"] = reporter
        reportRef.push().setValue(reportMap).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(mContext,"Post Reported",Toast.LENGTH_SHORT).show()
            }
        }



    }

    private fun deletePost(postID: String){
        val progressDialog = ProgressDialog(mContext)
        progressDialog.setTitle("Deleting Post")
        progressDialog.setMessage("Please Wait...")
        progressDialog.show()

        FirebaseDatabase.getInstance().reference.child("Posts").child(postID).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                progressDialog.dismiss()
                Toast.makeText(mContext,"Post Deleted",Toast.LENGTH_SHORT).show()
                val i = Intent(mContext,MainActivity::class.java)
                i.putExtra("openDasBoard","openDashBoard")
                mContext.startActivity(i)
            }
        }

    }

    private fun checkVerifiedAccount(image: ImageView,profileID:String){
        val ref = FirebaseDatabase.getInstance().reference.child("verified").child(profileID)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    image.visibility = View.VISIBLE
                }else{
                    image.visibility = View.GONE
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}