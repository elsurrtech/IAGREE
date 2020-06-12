package com.app.iagree.ui.home

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.agrawalsuneet.dotsloader.loaders.LazyLoader
import com.app.iagree.R
import com.app.iagree.adaptor.*
import com.app.iagree.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

public class HomeFragment : Fragment() {

    private val t1: TextView? = null
    private val t2: TextView? = null

    private var notificationList: List<requests>? = null
    private var notificationAdaptor: RequestAdaptor? = null
    val CHANNEL_ID = "com.app.iagree.ANDROID"


    private var postAdaptor: PostAdaptor? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<String>? = null
    private var storyAdaptor : StoryAdaptor? = null
    private var storyList: MutableList<Story>? = null

    private var leaderboardAdaptor: leaderboardAdaptor? = null
    private var leaderBoardList: MutableList<leaderboard>? = null

    var connectivity: ConnectivityManager? = null
    var info : NetworkInfo? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
            val view = inflater.inflate(R.layout.fragment_home, container, false)

        notificationList = ArrayList()
        notificationAdaptor = RequestAdaptor(requireContext(),notificationList as ArrayList<requests>)
        val asd = notificationAdaptor!!.itemCount
        if (asd>0){
            val imp = NotificationManager.IMPORTANCE_HIGH
            val mNotificationChannel = NotificationChannel(CHANNEL_ID,"noti",imp)
            val notificationBuilder: Notification.Builder = Notification.Builder(requireContext(),CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("New Request")
                .setContentText("You got $asd new Request")
                .setPriority(Notification.PRIORITY_DEFAULT)
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mNotificationChannel)
            notificationManager.notify(0,notificationBuilder.build())
        }


        //leaderBoard
        val viewPager : ViewPager2 = view.findViewById(R.id.leaderBoard_viewPager_fragment_home)

        leaderBoardList = ArrayList()
        leaderboardAdaptor = leaderboardAdaptor(requireContext(),leaderBoardList as MutableList<leaderboard>)
        viewPager.adapter = leaderboardAdaptor
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.offscreenPageLimit = 3
        viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val c = CompositePageTransformer()
        c.addTransformer(MarginPageTransformer(5))
        c.addTransformer(object :ViewPager2.PageTransformer{
            override fun transformPage(page: View, position: Float) {
                val r:Float = 1 - Math.abs(position)
                page.scaleY = 0.95f+ r*0.05f
            }

        })

        viewPager.setPageTransformer(c)


        var recyclerView: RecyclerView? = null
        recyclerView = view.findViewById(R.id.recyclerView_home_fragment)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = DefaultItemAnimator()

        postList = ArrayList()
        postAdaptor = context?.let { PostAdaptor(it,postList as ArrayList<Post>) }
        recyclerView.adapter = postAdaptor


        var recyclerViewStory : RecyclerView? = null
        recyclerViewStory = view.findViewById(R.id.recyclerView_story_home_fragment)
        val linearLayoutManagerStory = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        recyclerViewStory!!.layoutManager = linearLayoutManagerStory

        storyList = ArrayList()
        storyAdaptor = context?.let { StoryAdaptor(it,storyList as ArrayList<Story>) }
        recyclerViewStory.adapter = storyAdaptor



        val loaderDashboard = view.findViewById<RelativeLayout>(R.id.loader_layout_home)
        val loaderFragmentDashboard = view.findViewById<LinearLayout>(R.id.loader_fragment_home)

        val loader = LazyLoader(requireContext(),15,50, ContextCompat.getColor(requireContext(), R.color.white),
            ContextCompat.getColor(requireContext(), R.color.white),
            ContextCompat.getColor(requireContext(), R.color.white)).apply {
            animDuration = 1500
            firstDelayDuration = 100
            secondDelayDuration = 200
            interpolator = DecelerateInterpolator()
        }

        loaderFragmentDashboard.addView(loader)



        if (isConnected()){
            checkFollowings()
        }else{
            view.loader_fragment_home.visibility = View.GONE
            view.image_noPost.visibility = View.VISIBLE
        }

        return view
    }

    private fun checkFollowings() {

        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    loader_fragment_home?.visibility = View.GONE
                    (followingList as ArrayList<String>).clear()
                    for (snapshot in p0.children){
                        snapshot.key?.let { (followingList as ArrayList<String>).add(it) }
                    }

                    retrievePosts()
                    retrieveStories()


                    recyclerView_home_fragment.visibility = View.VISIBLE
                    leaderBoard_viewPager_fragment_home.visibility = View.GONE
                }else{
                    loader_layout_home?.visibility = View.GONE

                    recyclerView_home_fragment.visibility = View.GONE
                    leaderBoard_viewPager_fragment_home.visibility = View.VISIBLE
                    loadLeaderBoard()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun retrieveStories() {
        val storyRef = FirebaseDatabase.getInstance().reference.child("Story")

        storyRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val timeCurrent = System.currentTimeMillis()

                (storyList as ArrayList<Story>).clear()

                (storyList as ArrayList<Story>).add(Story("",0,0,"",FirebaseAuth.getInstance().currentUser!!.uid))

                loader_layout_home?.visibility = View.GONE

                for (id in followingList!!){
                    var countStory = 0
                    var story: Story? = null

                    for (snapshot in p0.child(id).children){
                        story=snapshot.getValue(Story::class.java)

                        if (timeCurrent>story!!.getTimeStart() && timeCurrent<story!!.getTimeEnd())
                        {
                            countStory++
                        }
                    }

                    if (countStory>0){
                        (storyList as ArrayList<Story>).add(story!!)
                    }
                }
                storyAdaptor!!.notifyDataSetChanged()
            }
        })
    }

    private fun retrievePosts(){

        val postRef = FirebaseDatabase.getInstance().reference
            .child("Posts")


        postRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    postList?.clear()
                    for (snapsot in p0.children){
                        val post = snapsot.getValue(Post::class.java)

                        for (userid in (followingList as ArrayList<String>) ){
                            if (post!!.getPublisher() == userid){
                                postList!!.add(post)
                            }

                            postAdaptor!!.notifyDataSetChanged()

                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun loadLeaderBoard(){
        val ref = FirebaseDatabase.getInstance().reference.child("leaderBoard")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){

                    leaderBoardList!!.clear()

                    for(snapshot in p0.children){
                        val x = snapshot.getValue(leaderboard::class.java)
                        leaderBoardList!!.add(x!!)
                    }

                    leaderboardAdaptor!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    fun isConnected(): Boolean{

        connectivity = requireContext().getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity!= null){
            info = connectivity!!.activeNetworkInfo
            if (info!= null){
                if (info!!.state == NetworkInfo.State.CONNECTED){
                    return true
                }
            }
        }


        return false
    }

}
