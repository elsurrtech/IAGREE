package com.app.iagree.questionare

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.R
import com.app.iagree.questionare.adaptor.questionAdaptor
import com.app.iagree.questionare.model.question
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_q_questions.view.*


class QQuestionsFragment : Fragment() {

    var questionList : MutableList<question>? = null
    var questionAdapter : questionAdaptor? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_q_questions, container, false)
        view.btnAdd_question.setOnClickListener {
            startActivity(Intent(context,AddQuestionActivity::class.java))
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView_q_questions)
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
        val ref = FirebaseDatabase.getInstance().reference.child("Questions")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                questionList!!.clear()
                for (x in p0.children){
                    val y = x.getValue(question::class.java)
                    if (y!!.getuid() == FirebaseAuth.getInstance().currentUser!!.uid){
                        questionList!!.add(y!!)
                    }

                }
                if (questionList!!.isEmpty()){
                    view?.recyclerView_q_questions?.visibility=View.GONE
                    view?.l1_noPost_q_questions?.visibility = View.VISIBLE
                }else{
                    view?.recyclerView_q_questions?.visibility=View.VISIBLE
                    view?.l1_noPost_q_questions?.visibility = View.GONE
                }
                questionAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


}