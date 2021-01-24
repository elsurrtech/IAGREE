package com.app.iagree.adaptor

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.R
import com.app.iagree.ShowEventActivity
import com.app.iagree.model.User
import com.app.iagree.model.event
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.internal.AddLastDesc
import java.lang.Exception

class EventsAdaptor(private var mContext:Context,private var mEvent:List<event>):RecyclerView.Adapter<EventsAdaptor.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
         var dateTime     = itemView.findViewById<TextView>(R.id.dateTimeEventLayout)
         var title        = itemView.findViewById<TextView>(R.id.titleEventLayout)
         var name         = itemView.findViewById<TextView>(R.id.nameEventLayout)
         var image: ImageView = itemView.findViewById<ImageView>(R.id.imageEventLayout)
         var peopleGoing  = itemView.findViewById<TextView>(R.id.peopleGoingEventLayout)
        var  onlineEvent  = itemView.findViewById<LinearLayout>(R.id.layoutOnlineEvent)
         var offlineEvent = itemView.findViewById<LinearLayout>(R.id.layoutOfflineEvent)
        var address = itemView.findViewById<TextView>(R.id.offlineEventAddress)

        var shimmer = itemView.findViewById<ShimmerFrameLayout>(R.id.shimmer)
        var linearLayoutShimmerFrameLayout : LinearLayout = itemView.findViewById(R.id.linearLayoutShimmer)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.event_layout,null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mEvent!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //start Shimmer
        holder.shimmer.startShimmer()

        //Firebase Current User
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        //event Model
        val event = mEvent[position]

        //Time and Date
        getDateTime(holder.dateTime,event.getDate(),event.geteventTime())

        //Title and Publisher Name
        getTitleAndName(holder.title,holder.name,event.getTitle(),event.getPublisher())

        //Total People Going
        getHowManyPeopleGoing(event.geteventID(),holder.peopleGoing)

        //Online or Offline
        getOnlineOrOffline(holder.address,holder.onlineEvent,holder.offlineEvent,event.getAddress())

        //getImage and close Shimmer
        if (event.getpostImage()!=""){
            Picasso.get().load(event.getpostImage())
                .resize(300,300).onlyScaleDown()
                .into(holder.image, object : com.squareup.picasso.Callback{
                    override fun onSuccess() {
                        holder.shimmer.stopShimmer()
                        holder.shimmer.setShimmer(null)

                        holder.image.setBackgroundResource(R.color.black)

                        holder.dateTime.setBackgroundResource(R.color.black)
                        holder.dateTime.setTextColor(ContextCompat.getColor(mContext,R.color.white))

                        holder.title.setBackgroundResource(R.color.black)
                        holder.title.setTextColor(ContextCompat.getColor(mContext,R.color.white))

                        holder.name.setBackgroundResource(R.color.black)
                        holder.title.setTextColor(ContextCompat.getColor(mContext,R.color.white))

                        holder.address.setTextColor(ContextCompat.getColor(mContext,R.color.grey))

                        holder.linearLayoutShimmerFrameLayout.setBackgroundResource(R.color.black)
                    }

                    override fun onError(e: Exception?) {

                    }
                })
        }else{
            holder.shimmer.stopShimmer()
            holder.shimmer.setShimmer(null)

            holder.image.setBackgroundResource(R.color.black)

            holder.dateTime.setBackgroundResource(R.color.black)
            holder.dateTime.setTextColor(ContextCompat.getColor(mContext,R.color.white))

            holder.title.setBackgroundResource(R.color.black)
            holder.title.setTextColor(ContextCompat.getColor(mContext,R.color.white))

            holder.name.setBackgroundResource(R.color.black)
            holder.title.setTextColor(ContextCompat.getColor(mContext,R.color.white))

            holder.address.setTextColor(ContextCompat.getColor(mContext,R.color.grey))

            holder.linearLayoutShimmerFrameLayout.setBackgroundResource(R.color.black)
        }

        holder.itemView.setOnClickListener {
            val i = Intent(mContext,ShowEventActivity::class.java)
            i.putExtra("eventID",event.geteventID())
            i.putExtra("eventPublisherID",event.getPublisher())
            mContext.startActivity(i)
        }



    }

    private fun getDateTime(T:TextView,d:String,t:String){
        T.text ="$d, $t"
    }

    private fun getTitleAndName(Title: TextView,Name:TextView,title:String, publisher : String){
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(publisher)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                Name.text = user!!.getFullname()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        Title.text = title
    }

    private fun getHowManyPeopleGoing(eventID:String, going:TextView){
        val ref  = FirebaseDatabase.getInstance().reference.child("Event Attendees").child(eventID)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                going.text = p0.childrenCount.toString() + " going"
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun getOnlineOrOffline(Address:TextView,OnlineLayout:LinearLayout,OfflineLayout:LinearLayout,address:String){
        if (address == "Online Event"){
            OnlineLayout.visibility = View.VISIBLE
            OfflineLayout.visibility = View.GONE
        }else{
            OnlineLayout.visibility = View.GONE
            OfflineLayout.visibility = View.VISIBLE
            Address.text = address
        }
    }


}