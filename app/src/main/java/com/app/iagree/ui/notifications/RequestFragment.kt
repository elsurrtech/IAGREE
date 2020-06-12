package com.app.iagree.ui.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.R
import com.app.iagree.adaptor.RequestAdaptor
import com.app.iagree.model.Comment
import com.app.iagree.model.requests
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_request.view.*

class RequestFragment : Fragment() {

    private var requestList: MutableList<requests>? = null
    private var requestAdaptor: RequestAdaptor? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_request, container, false)



        val recyclerView : RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView_request_fragment)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager

        requestList = ArrayList()
        requestAdaptor = RequestAdaptor(requireContext(),requestList)
        recyclerView.adapter = requestAdaptor

        readRequests(view.noRequests)


        return view
    }

    private fun readRequests(r:RelativeLayout){

        val requestRef = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("Requests")
        requestRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    r.visibility = View.GONE
                    requestList!!.clear()
                    for (snapshot in p0.children){
                        val x = snapshot.getValue(requests::class.java)
                        requestList!!.add(x!!)
                    }

                    requestAdaptor!!.notifyDataSetChanged()
                }else{
                    r.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

}