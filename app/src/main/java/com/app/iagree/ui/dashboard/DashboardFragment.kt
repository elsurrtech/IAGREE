package com.app.iagree.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.renderscript.Sampler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agrawalsuneet.dotsloader.loaders.LazyLoader
import com.app.iagree.EditProfileActivity
import com.app.iagree.R
import com.app.iagree.ShowUsersWhoLikedPostActivity
import com.app.iagree.adaptor.MyImagesAdaptor
import com.app.iagree.model.Post
import com.app.iagree.model.User
import com.app.iagree.settings.SettingsActivity
import com.flaviofaria.kenburnsview.KenBurnsView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jgabrielfreitas.core.BlurImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class DashboardFragment : Fragment() {

    private lateinit var profileID: String
    private lateinit var firebaseUser: FirebaseUser

    var postList: List<Post>? = null
    var myImagesAdaptor: MyImagesAdaptor? = null
    var myImagesAdaptorSave: MyImagesAdaptor? = null
    var postListSave : List<Post>? = null
    var mySavesImage: List<String>? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        root.image_verified_dashboard.visibility = View.GONE

        //editButton  and btnSettings
        val editButton = root.findViewById<Button>(R.id.editProfile)
        editButton.setOnClickListener {
            val i = Intent(context,EditProfileActivity::class.java)
            startActivity(i)
        }

        val btnSettings = root.findViewById<ImageView>(R.id.btnSetting)
        btnSettings.setOnClickListener {
            val i = Intent(context,SettingsActivity::class.java)
            startActivity(i)
        }

        //
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        profileID = firebaseUser.uid

        //
         root.show_followers.setOnClickListener {
                    val i = Intent(context,ShowUsersWhoLikedPostActivity::class.java)
                    i.putExtra("id",profileID)
                    i.putExtra("title","Followers")
                    startActivity(i)
               }

        root.show_following.setOnClickListener {
            val i = Intent(context,ShowUsersWhoLikedPostActivity::class.java)
            i.putExtra("id",profileID)
            i.putExtra("title","Following")
            startActivity(i)
        }




        checkFollowAndFollowingButtonStatus()
        //recyclerView for Uploads
        val recyclerViewUploads: RecyclerView
        recyclerViewUploads = root.findViewById(R.id.recyclerView_uploads_fragment_dashboard)
        recyclerViewUploads.setHasFixedSize(true)
        recyclerViewUploads.isNestedScrollingEnabled = false
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context,3)
        recyclerViewUploads.layoutManager = linearLayoutManager

        postList = ArrayList()
        myImagesAdaptor = context?.let { MyImagesAdaptor(it,postList as ArrayList<Post> )}
        recyclerViewUploads.adapter = myImagesAdaptor


        //recyclerView for Saves
        val recyclerViewSaves: RecyclerView
        recyclerViewSaves = root.findViewById(R.id.recyclerView_saved_fragment_dashboard)
        recyclerViewSaves.setHasFixedSize(true)
        recyclerViewSaves.isNestedScrollingEnabled = false
        val linearLayoutManagerSave: LinearLayoutManager = GridLayoutManager(context,3)
        recyclerViewSaves.layoutManager = linearLayoutManagerSave

        postListSave = ArrayList()
        myImagesAdaptorSave = context?.let { MyImagesAdaptor(it,postListSave as ArrayList<Post> )}
        recyclerViewSaves.adapter = myImagesAdaptorSave


        //Recycler View`s OnClick
        val saveImageRecyclerView :ImageView
        saveImageRecyclerView = root.findViewById(R.id.show_saves)
        saveImageRecyclerView.setOnClickListener {
            recyclerViewSaves.visibility = View.VISIBLE
            root.show_posts.setImageResource(R.drawable.ic_grain_black_24dp)
            saveImageRecyclerView.setImageResource(R.drawable.ic_archive_white_24dp)
            recyclerViewUploads.visibility = View.GONE
        }

        val uploadImageRecyclerView :ImageView
        uploadImageRecyclerView = root.findViewById(R.id.show_posts)
        uploadImageRecyclerView.setOnClickListener {
            recyclerViewSaves.visibility = View.GONE
            uploadImageRecyclerView.setImageResource(R.drawable.ic_grain_white_24dp)
            saveImageRecyclerView.setImageResource(R.drawable.ic_archive_black_24dp)
            recyclerViewUploads.visibility = View.VISIBLE
        }

        val loaderDashboard = root.findViewById<RelativeLayout>(R.id.loader_layout_dashboard)
        val loaderFragmentDashboard = root.findViewById<LinearLayout>(R.id.loader_fragment_dashboard)



        getFollowers()
        getFollowing()
        userInfo(loaderFragmentDashboard,loaderDashboard)
        myPhotos()
        getTotalNumberOfPosts()
        mySaves()
        checkVerifiedAccount(root.image_verified_dashboard)

        return root
    }

    private fun mySaves() {
        mySavesImage = ArrayList()

        val savesRef = FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser.uid)
        savesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    (mySavesImage as ArrayList<String>).clear()
                    for (snapsot in p0.children){
                        (mySavesImage as ArrayList<String>).add(snapsot.key!!)
                    }
                    readSavedImageData()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun readSavedImageData() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    (postListSave as ArrayList<Post>).clear()
                    for (snapshot in p0.children){
                        val post = snapshot.getValue(Post::class.java)
                        for (key in mySavesImage!!){
                            if (post!!.getPostID() == key){
                                (postListSave as ArrayList<Post>).add(post!!)
                            }
                        }
                    }

                    myImagesAdaptorSave!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun myPhotos(){
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    (postList as ArrayList<Post>).clear()

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

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun checkFollowAndFollowingButtonStatus() {

         firebaseUser.uid.let { it->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }


    }

    private fun getFollowers(){

        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileID)
                .child("Followers")


        followersRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    view?.total_followers?.text = p0.childrenCount.toString()

                }

            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })
    }

    private fun getFollowing(){

        val followingRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileID)
                .child("Following")

        followingRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    view?.total_following?.text = p0.childrenCount.toString()

                }

            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })
    }

    private fun userInfo(l:LinearLayout,r:RelativeLayout){

        val loader = LazyLoader(requireContext(),15,50, ContextCompat.getColor(requireContext(), R.color.white),
            ContextCompat.getColor(requireContext(), R.color.white),
            ContextCompat.getColor(requireContext(), R.color.white)).apply {
            animDuration = 1500
            firstDelayDuration = 100
            secondDelayDuration = 200
            interpolator = DecelerateInterpolator()
        }

        l.addView(loader)

        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(profileID)
        usersRef.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {


                val b = view?.findViewById<KenBurnsView>(R.id.blur_image)

                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).into(view?.profileImage)
                    Picasso.get().load(user!!.getImage()).into(b, object :com.squareup.picasso.Callback{
                        override fun onSuccess() {
                            r.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {

                        }
                    })
                    view?.username_dashboard_frag?.text = user!!.getUsername()
                    view?.fullName_dashboard_frag?.text = user!!.getFullname()
                    view?.bio_dashboard_frag?.text = user!!.getBio()

                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })
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
                    total_posts.text = postCounter.toString()
                }else{
                    total_posts.text = "0"
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

    private fun checkVerifiedAccount(image: ImageView){
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
