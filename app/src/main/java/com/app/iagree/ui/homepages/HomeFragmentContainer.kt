package com.app.iagree.ui.homepages

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.app.iagree.R
import com.app.iagree.adaptor.HomePagesAdaptor
import com.app.iagree.messages.MessageContainerActivity
import com.app.iagree.questionare.QuestionActivity
import kotlinx.android.synthetic.main.fragment_home_container.view.*

class HomeFragmentContainer : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_container, container, false)

        view.viewPager.adapter = HomePagesAdaptor(childFragmentManager)
        view.viewPager.currentItem = 0
        view.r2.setupWithViewPager(view.viewPager)

        view.ic_messages_fragment_home_container.setOnClickListener {
            val i = Intent(context, MessageContainerActivity::class.java)
            startActivity(i)
        }

        view.ic_questionaire_fragment_home_container.setOnClickListener {
            startActivity(Intent(context,QuestionActivity::class.java))
        }

        return view
    }


}