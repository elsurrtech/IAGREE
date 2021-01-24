package com.app.iagree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.view.View
import androidx.core.content.ContextCompat
import com.app.iagree.model.User
import com.app.iagree.model.event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_show_event.*
import java.lang.Exception

class ShowEventActivity : AppCompatActivity() {

    private var eventID:String? = ""
    private var eventPublisherID:String? = ""
    private var firebaseUser:FirebaseUser? = null
    private var peopleAllowed = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_event)

        shimmer_show_event.startShimmer()

        val intent = intent
        eventID = intent.getStringExtra("eventID")
        eventPublisherID = intent.getStringExtra("eventPublisherID")

        firebaseUser = FirebaseAuth.getInstance()!!.currentUser


        getEventInfo()
        getPublisherInfo()
        currentUserInfo()
        getEventAttendees()

    }

    private fun getEventInfo(){
        val ref = FirebaseDatabase.getInstance().reference.child("Events").child(eventID!!)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val event = p0.getValue(event::class.java)
                title_show_event?.text = event!!.getTitle()
                date_show_event?.text = event.geteventDate()
                time_show_event?.text = event.geteventTime()
                address_show_event?.text = event.getAddress()
                description_show_event?.text = event.getDescription()
                peopleAllowed = event?.getmaxPeople()
                if (event.getFees() == "Free"){
                    entryFees_show_event?.text = event.getFees()
                }else{
                    entryFees_show_event?.text = event.getFees() + " €"
                }

                if (event.getpostImage()!=""){
                    image_show_event?.visibility = View.VISIBLE
                    Picasso.get().load(event.getpostImage()).into(image_show_event!!, object :com.squareup.picasso.Callback{
                        override fun onSuccess() {
                            shimmer_show_event!!.stopShimmer()
                            shimmer_show_event!!.setShimmer(null)

                            title_show_event.setTextColor(ContextCompat.getColor(this@ShowEventActivity,R.color.white))
                            date_show_event.setTextColor(ContextCompat.getColor(this@ShowEventActivity,R.color.white))
                            time_show_event.setTextColor(ContextCompat.getColor(this@ShowEventActivity,R.color.white))
                            address_show_event.setTextColor(ContextCompat.getColor(this@ShowEventActivity,R.color.white))
                            description_show_event.setTextColor(ContextCompat.getColor(this@ShowEventActivity,R.color.white))
                            relative_show_event!!.visibility = View.VISIBLE
                            r3_show_event.visibility = View.VISIBLE
                            r3_show_event.setBackgroundResource(R.color.bgColor)
                            linear_show_event.setBackgroundResource(R.color.black)
                            r2_show_event.visibility = View.VISIBLE
                            profile_image_comment_show_event.setBackgroundResource(R.color.bgColor)
                            profilePhoto_show_event.setBackgroundResource(R.color.black)
                            checkComments(event.geteventID())

                        }

                        override fun onError(e: Exception?) {

                        }
                    })
                }else{
                    shimmer_show_event!!.stopShimmer()
                    shimmer_show_event!!.setShimmer(null)

                    title_show_event.setTextColor(ContextCompat.getColor(this@ShowEventActivity,R.color.white))
                    date_show_event.setTextColor(ContextCompat.getColor(this@ShowEventActivity,R.color.white))
                    time_show_event.setTextColor(ContextCompat.getColor(this@ShowEventActivity,R.color.white))
                    address_show_event.setTextColor(ContextCompat.getColor(this@ShowEventActivity,R.color.white))
                    description_show_event.setTextColor(ContextCompat.getColor(this@ShowEventActivity,R.color.white))
                    relative_show_event!!.visibility = View.VISIBLE
                    r3_show_event.visibility = View.VISIBLE
                    r3_show_event.setBackgroundResource(R.color.bgColor)
                    linear_show_event.setBackgroundResource(R.color.black)
                    r2_show_event.visibility = View.VISIBLE
                    profile_image_comment_show_event.setBackgroundResource(R.color.bgColor)
                    profilePhoto_show_event.setBackgroundResource(R.color.black)
                    checkComments(event.geteventID())
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun getPublisherInfo(){
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(eventPublisherID!!)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                name_show_event.text = user!!.getFullname()
                Picasso.get().load(user!!.getImage()).into(profilePhoto_show_event)
                getTotalFollowers()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun getTotalFollowers(){
        val ref  = FirebaseDatabase.getInstance().reference.child("Follow").child(eventPublisherID!!).child("Followers")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                followers_show_event.text = p0.childrenCount.toString() + " Followers"
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun currentUserInfo(){
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                Picasso.get().load(user!!.getImage()).into(profile_image_comment_show_event)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun getEventAttendees(){
        val ref = FirebaseDatabase.getInstance().reference.child("eventAttendees").child(eventID!!)
        ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val x = p0.childrenCount.toInt() //already going
                    var y = peopleAllowed.toInt()    //allowed

                    var z = y - x

                    peopleGoing_show_event.text = z.toString() + " spots left"
                }

                else{
                    peopleGoing_show_event.text = peopleAllowed!!.toString()  + " spots left"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun checkComments(eventID:String){
        val ref = FirebaseDatabase.getInstance().reference.child("eventComments").child(eventID)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    linear_nopost_show_event?.visibility = View.GONE
                }else{
                    linear_nopost_show_event?.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}