package com.app.iagree

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.whenCreated
import com.agrawalsuneet.dotsloader.loaders.LazyLoader
import com.app.iagree.adaptor.StoryAdaptor
import com.app.iagree.model.Story
import com.app.iagree.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.android.synthetic.main.activity_story.*
import java.lang.Exception

class StoryActivity : AppCompatActivity() {

    var currentUserID: String = ""
    var userid: String = ""
    var counter = 0
    var pressTime = 0L
    var limit = 500L

    var imageList: List<String>? = null
    var storyIdList: List<String>? = null

    var storyProgressView: StoriesProgressView? = null

    private val onTouchListener = View.OnTouchListener { v, event ->
        when(event.action){
            MotionEvent.ACTION_DOWN ->{
                pressTime = System.currentTimeMillis()
                storyProgressView!!.pause()
                return@OnTouchListener false
            }

            MotionEvent.ACTION_UP ->{
                val now = System.currentTimeMillis()
                storyProgressView!!.resume()
                return@OnTouchListener limit < now - pressTime
            }
        }

        false
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        currentUserID =FirebaseAuth.getInstance().currentUser!!.uid
        userid = intent.getStringExtra("userid")!!

        storyProgressView = findViewById(R.id.stories_progress)

        layout_seen.visibility = View.GONE
        layout_delete.visibility = View.GONE

        if (userid == currentUserID){
            layout_seen.visibility = View.VISIBLE
            layout_delete.visibility = View.VISIBLE
        }

        getStories(userid)
        userInfo(userid)

        val reverse: View = findViewById(R.id.reverse)
        reverse.setOnClickListener {
            storyProgressView!!.reverse()
        }
        reverse.setOnTouchListener(onTouchListener)

        val skip: View = findViewById(R.id.skip)
        skip.setOnClickListener {
            storyProgressView!!.skip()
        }
        skip.setOnTouchListener(onTouchListener)

        seen_number.setOnClickListener {
            val i = Intent(this, ShowUsersWhoLikedPostActivity::class.java)
            i.putExtra("id",userid)
            i.putExtra("storyid",storyIdList!![counter])
            i.putExtra("title","views")
            startActivity(i)
        }

        story_delete.setOnClickListener {
            val ref  = FirebaseDatabase.getInstance().reference.child("Story")
                .child(userid!!).child(storyIdList!![counter])

            ref.removeValue().addOnCompleteListener { task->
                if (task.isSuccessful){
                    val i = Intent(this,MainActivity::class.java)
                    startActivity(i)
                    Toast.makeText(this,"Story Deleted",Toast.LENGTH_SHORT).show()
                }

            }
        }

    }

    private fun seenNumber(storyid: String){
        val ref = FirebaseDatabase.getInstance().reference.child("Story")
            .child(userid).child(storyid).child("views")

        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                seen_number.text =  p0.childrenCount.toString()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun addViewToStory(storyid: String){
        FirebaseDatabase.getInstance().reference.child("Story")
            .child(userid).child(storyid).child("views").child(currentUserID).setValue(true)
    }

    private fun userInfo(userid:String){
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userid)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.ic_account_circle_black_24dp).into(story_profile_image)
                    story_username.text = user.getUsername()
                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })
    }

    private fun getStories(userid: String){
        imageList = ArrayList()
        storyIdList = ArrayList()

        val loader = LazyLoader(this,15,50, ContextCompat.getColor(this, R.color.white),
            ContextCompat.getColor(this, R.color.white),
            ContextCompat.getColor(this, R.color.white)).apply {
            animDuration = 1500
            firstDelayDuration = 100
            secondDelayDuration = 200
            interpolator = DecelerateInterpolator()
        }

        loader_container.addView(loader)

        val ref = FirebaseDatabase.getInstance().reference.child("Story").child(userid)

        ref.addValueEventListener(object : ValueEventListener, StoriesProgressView.StoriesListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    (imageList as ArrayList<String>).clear()
                    (storyIdList as ArrayList<String>).clear()

                    for (snapshot in p0.children){
                        val story: Story?= snapshot.getValue<Story>(Story::class.java)
                        val timeCurrent = System.currentTimeMillis()

                        if(timeCurrent>story!!.getTimeStart() && timeCurrent<story.getTimeEnd()){
                            (imageList as ArrayList<String>).add(story.getImageUrl())
                            (storyIdList as ArrayList<String>).add(story.getStoryID())
                        }
                    }

                    storyProgressView!!.setStoriesCount((imageList as ArrayList<String>).size)
                    storyProgressView!!.setStoryDuration(5000L)
                    storyProgressView!!.setStoriesListener(this)
                    image_container.visibility = View.GONE

                    Picasso.get().load(imageList!!.get(counter)).into(image_story, object : com.squareup.picasso.Callback{
                        override fun onSuccess() {
                            image_container.visibility = View.VISIBLE
                            loader_container.removeView(loader)
                            storyProgressView!!.startStories(counter!!)
                        }

                        override fun onError(e: Exception?) {

                        }
                    })


                    addViewToStory(storyIdList!!.get(counter))
                    seenNumber(storyIdList!!.get(counter))
                }
                else{
                    val i = Intent(this@StoryActivity,MainActivity::class.java)
                    startActivity(i)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onComplete() {
                finish()
            }

            override fun onPrev() {
                if (counter-1<0) return
                Picasso.get().load(imageList!![--counter]).into(image_story)
                seenNumber(storyIdList!![counter])
            }

            override fun onNext() {
                Picasso.get().load(imageList!![++counter]).into(image_story)
                addViewToStory(storyIdList!![counter])
                seenNumber(storyIdList!![counter])
            }
        })
    }

    override fun onResume() {
        super.onResume()
        storyProgressView!!.resume()

    }

    override fun onDestroy() {
        super.onDestroy()
        storyProgressView!!.destroy()
    }

    override fun onPause() {
        super.onPause()
        storyProgressView!!.pause()
    }
}
