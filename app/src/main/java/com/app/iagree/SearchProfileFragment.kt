package com.app.iagree

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.adaptor.MyImagesAdaptor
import com.app.iagree.model.Post
import com.app.iagree.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_search_profile.*
import kotlinx.android.synthetic.main.fragment_search_profile.view.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class SearchProfileFragment : Fragment() {

    var postList: List<Post>? = null
    var myImagesAdaptor: MyImagesAdaptor? = null

    private lateinit var mUser: List<User>

    private lateinit var profileID: String
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_profile, container, false)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref!=null){
            profileID = pref.getString("profileID","profileID").toString()
        }

        val btnFollowUnfollow = view.findViewById<Button>(R.id.follow_unFollow_search_profile_frag)

        btnFollowUnfollow.setOnClickListener {

            if (follow_unFollow_search_profile_frag.text.toString() == "Follow"){

                firebaseUser.uid.let { it->
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(it.toString()).child("Following").child(profileID)
                        .setValue(true)
                }

                firebaseUser.uid.let { it->
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(profileID).child("Followers").child(it.toString())
                        .setValue(true)
                }

                addNotification()

                follow_unFollow_search_profile_frag.text = "Following"
            }
            else if(follow_unFollow_search_profile_frag.text.toString() == "Following"){

                firebaseUser.uid.let { it->
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(it.toString()).child("Following").child(profileID)
                        .removeValue()
                }

                firebaseUser.uid.let { it->
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(profileID).child("Followers").child(it.toString())
                        .removeValue()
                }

               // follow_unFollow_search_profile_frag.text = "Follow"

            }
            else if(follow_unFollow_search_profile_frag.text.toString() == "Request"){
                firebaseUser?.uid.let { it->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(profileID)
                        .child("Requests").child(it.toString()).child("requestPublisher")
                        .setValue(it.toString()).addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                follow_unFollow_search_profile_frag.text = "Requested"
                            }
                        }
                }

            }
            else if(follow_unFollow_search_profile_frag.text.toString() == "Requested"){
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

        view.show_followers_search.setOnClickListener {
            val i = Intent(context,ShowUsersWhoLikedPostActivity::class.java)
            i.putExtra("id",profileID)
            i.putExtra("title","Followers")
            startActivity(i)
        }

        view.show_following_search.setOnClickListener {
            val i = Intent(context,ShowUsersWhoLikedPostActivity::class.java)
            i.putExtra("id",profileID)
            i.putExtra("title","Following")
            startActivity(i)
        }

       //checkFollowAndFollowingButtonStatus
        val FollowfollowingRef = firebaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }

        FollowfollowingRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(profileID).exists()){
                    view?.follow_unFollow_search_profile_frag?.text = "Following"
                }
                else{
                    val ref  = FirebaseDatabase.getInstance().reference.child("PrivateAccounts")
                    ref.addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            if (dataSnapshot.child(profileID).exists()){
                                val requestedRef = FirebaseDatabase.getInstance().reference
                                    .child("Follow").child(profileID)
                                    .child("Requests").child(firebaseUser!!.uid)
                                requestedRef.addValueEventListener(object :ValueEventListener{
                                    override fun onDataChange(goti: DataSnapshot) {
                                        if (goti.exists()){
                                            view?.follow_unFollow_search_profile_frag?.text = "Requested"
                                        }else{
                                            view?.follow_unFollow_search_profile_frag?.text = "Request"
                                        }
                                    }

                                    override fun onCancelled(p0: DatabaseError) {

                                    }
                                })

                            }else{
                                view?.follow_unFollow_search_profile_frag?.text = "Follow"
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



        //getFollowers
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileID)
            .child("Followers")
        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    view?.total_followers_search_profile_frag?.text = p0.childrenCount.toString()

                }

            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })

        //getFollowing
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileID)
            .child("Following")
        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    view?.total_following_search_profile_frag?.text = p0.childrenCount.toString()

                }

            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })

        //userInfo
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(profileID)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).into(view?.image_search_profile_frag)
                    Picasso.get().load(user!!.getImage()).into(view?.blur_image_search_profile,object :com.squareup.picasso.Callback{
                        override fun onSuccess() {
                            pref!!.edit().clear().apply()
                        }

                        override fun onError(e: Exception?) {

                        }
                    })
                    view?.username_search_profile_frag?.text = user!!.getUsername()
                    view?.fullName_search_profile_frag?.text = user!!.getFullname()
                    view?.bio_search_profile_frag?.text = user!!.getBio()
                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })

        view.accountPrivacy_searchProfile.visibility = View.GONE

        //recyclerView for Uploads
        val recyclerViewUploads: RecyclerView
        recyclerViewUploads = view.findViewById(R.id.recyclerView_uploads_fragment_search_profile)
        recyclerViewUploads.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context,3)
        recyclerViewUploads.layoutManager = linearLayoutManager

        postList = ArrayList()
        myImagesAdaptor = context?.let { MyImagesAdaptor(it,postList as ArrayList<Post> ) }
        recyclerViewUploads.adapter = myImagesAdaptor

        getTotalNumberOfPosts()
        myPhotos(recyclerViewUploads,view.accountPrivacy_searchProfile)


        return view
    }

    private fun getTotalNumberOfPosts(){
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    var postCounter = 0
                    for (snapshot in p0.children){
                        val post = snapshot.getValue(Post::class.java)
                        if (post?.getPublisher() == profileID){
                            postCounter++
                        }
                    }
                    total_posts_search_profile?.text = postCounter.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun myPhotos(r:RecyclerView,l:LinearLayout){
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){

                    (postList as ArrayList<Post>).clear()

                    val privacyRef = FirebaseDatabase.getInstance().reference.child("PrivateAccounts")
                    privacyRef.addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(goti: DataSnapshot) {
                            if (goti.child(profileID).exists()){

                                val followersRef = FirebaseDatabase.getInstance().reference
                                    .child("Follow").child(profileID)
                                    .child("Followers")
                                followersRef.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(gola: DataSnapshot) {

                                        if (gola.child(firebaseUser!!.uid).exists()){

                                            for (snapshot in p0.children){
                                                val post = snapshot.getValue(Post::class.java)!!
                                                if (post.getPublisher().equals(profileID)){
                                                    (postList as ArrayList<Post>).add(post)
                                                }

                                                Collections.reverse(postList)
                                                myImagesAdaptor!!.notifyDataSetChanged()

                                            }

                                        }else{
                                            show_followers_search?.isClickable = false
                                            show_following_search?.isClickable = false
                                            r.visibility = View.GONE
                                            l.visibility = View.VISIBLE
                                        }

                                    }

                                    override fun onCancelled(gola: DatabaseError) {


                                    }
                                })

                            }else{
                                for (snapshot in p0.children){
                                    val post = snapshot.getValue(Post::class.java)!!
                                    if (post.getPublisher().equals(profileID)){
                                        (postList as ArrayList<Post>).add(post)
                                    }

                                    Collections.reverse(postList)
                                    myImagesAdaptor!!.notifyDataSetChanged()

                                }
                            }

                        }

                        override fun onCancelled(goti: DatabaseError) {

                        }
                    })


                }else{
                    //as
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun addNotification(){
        val notiref = FirebaseDatabase.getInstance().reference
            .child("Notifications").child(profileID)

        val notiMap = HashMap<String, Any?>()
        notiMap["userID"] = firebaseUser!!.uid
        notiMap["text"] = "started following you"
        notiMap["postID"] = ""
        notiMap["isPost"] = false

        notiref.push().setValue(notiMap)

    }

    override fun onDetach() {
        super.onDetach()
    }

}

