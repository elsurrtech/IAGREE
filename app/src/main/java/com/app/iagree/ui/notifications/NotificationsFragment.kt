package com.app.iagree.ui.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.R
import com.app.iagree.adaptor.NotificationAdaptor
import com.app.iagree.adaptor.RequestAdaptor
import com.app.iagree.model.notification
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class NotificationsFragment : Fragment() {

    private var notificationList: List<notification>? = null
    private var notificationAdaptor: NotificationAdaptor? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
                  ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)

        var recyclerView:RecyclerView = root.findViewById(R.id.recyclerView_notification_fragment)
        recyclerView?.setHasFixedSize(true)
        recyclerView.layoutManager =LinearLayoutManager(context)

        notificationList = ArrayList()
        notificationAdaptor = NotificationAdaptor(requireContext(),notificationList as ArrayList<notification>)
        recyclerView.adapter = notificationAdaptor

        readNotifications()

        return root
    }

    private fun readNotifications(){
        val notiRef = FirebaseDatabase.getInstance().reference
            .child("Notifications").child(FirebaseAuth.getInstance().currentUser!!.uid)

        notiRef.addValueEventListener(object :ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    (notificationList as ArrayList<notification>).clear()

                    for (snapshot in p0.children){
                        if(snapshot.child("userID").value != FirebaseAuth.getInstance().currentUser!!.uid){
                            val notification = snapshot.getValue(notification::class.java)
                            (notificationList as ArrayList<notification>).add(notification!!)
                        }

                    }

                    Collections.reverse(notificationList)

                    notificationAdaptor!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }
}
