package com.app.iagree.questionare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.R
import com.app.iagree.adaptor.PostAdaptor
import com.app.iagree.model.Post
import com.app.iagree.questionare.adaptor.questionAdaptor
import com.app.iagree.questionare.model.question
import com.google.firebase.database.*


class QHomeFragment : Fragment() {

    var questionList : MutableList<question>? = null
    var questionAdapter : questionAdaptor? = null
    private var isScrolling = false
    private var layoutManager : RecyclerView.LayoutManager? = null

    var last_node = ""
    var last_key = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_q_home, container, false)

        val recyclerView:RecyclerView = view.findViewById(R.id.recyclerView_q_home)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = DefaultItemAnimator()

        questionList = ArrayList()
        questionAdapter = questionAdaptor(requireContext(),questionList!!)
        recyclerView.adapter = questionAdapter

        retreiveQuestions()

        return view
    }

    private fun retreiveQuestions(){
        val ref : Query

            ref = FirebaseDatabase.getInstance().reference.child("Questions")

        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                questionList!!.clear()
                for (x in p0.children){
                    val y = x.getValue(question::class.java)
                    questionList!!.add(y!!)
                }
                questionAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }



}