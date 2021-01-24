package com.app.iagree.messages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.R
import com.app.iagree.adaptor.ConfessionsAdaptor
import com.app.iagree.adaptor.RequestAdaptor
import com.app.iagree.model.confessions
import com.app.iagree.model.requests
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_confessions.*
import kotlinx.android.synthetic.main.fragment_confessions.view.*


class ConfessionsFragment : Fragment() {

    private var confessionList: MutableList<confessions>? = null
    private var confessionAdaptor: ConfessionsAdaptor? = null

    private var keyList: MutableList<String>? =null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_confessions, container, false)

        val recyclerView : RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView_fragment_confessions)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager

        confessionList = ArrayList()
        keyList = ArrayList()
        confessionAdaptor = ConfessionsAdaptor(requireContext(),keyList,confessionList)
        recyclerView.adapter = confessionAdaptor

        view.r1_confession?.visibility = View.VISIBLE

        readConfessions(view.r1_confession!!,recyclerView)

        return view
    }

    private fun readConfessions(r:RelativeLayout,recyclerView: RecyclerView){
        val confessionsRef = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("ConfessionRequests")
        confessionsRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    recyclerView?.visibility = View.VISIBLE
                    r.visibility = View.GONE
                        confessionList!!.clear()
                        keyList!!.clear()
                       for (y in p0.children){
                           val x = y.getValue(confessions::class.java)
                           val s= y.key
                           confessionList!!.add(x!!)
                           keyList!!.add(s!!)
                       }

                    confessionAdaptor!!.notifyDataSetChanged()
                }else{

                    recyclerView?.visibility = View.GONE
                    r.visibility = View.VISIBLE

                }



            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

}