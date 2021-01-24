package com.app.iagree.messages

import android.media.session.MediaSession
import android.os.Bundle
import android.renderscript.Sampler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.Notifications.Token
import com.app.iagree.R
import com.app.iagree.adaptor.MessageFragmentItemAdaptor
import com.app.iagree.adaptor.RequestAdaptor
import com.app.iagree.model.MessageList
import com.app.iagree.model.requests
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId

class MessagesFragment : Fragment() {

    private var messageList: MutableList<MessageList>? = null
    private var messageItemAdaptor: MessageFragmentItemAdaptor? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_messages, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_messages_fragment)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager

        messageList = ArrayList()
        messageItemAdaptor = MessageFragmentItemAdaptor(requireContext(),messageList)
        recyclerView.adapter = messageItemAdaptor

        readMessageList()

        updateToken(FirebaseInstanceId.getInstance().token)

        return view
    }

    private fun updateToken(token: String?) {

        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(token1)

    }

    private fun readMessageList(){
        val ref= FirebaseDatabase.getInstance().reference
            .child("MessageList").child(FirebaseAuth.getInstance().currentUser!!.uid)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    messageList!!.clear()
                    for (x in p0.children){
                        val y = x.getValue(MessageList::class.java)
                        messageList!!.add(y!!)
                    }
                    messageItemAdaptor!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


}