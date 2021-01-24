package com.app.iagree.questionare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.iagree.R
import com.app.iagree.adaptor.HomePagesAdaptor
import com.app.iagree.questionare.adaptor.questionPagesAdaptor
import kotlinx.android.synthetic.main.fragment_home_container.view.*
import kotlinx.android.synthetic.main.fragment_q_pages.view.*


class QPagesFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_q_pages, container, false)

        view.viewPagerQuestion.adapter = questionPagesAdaptor(childFragmentManager)
        view.viewPagerQuestion.currentItem = 0
        view.r2Question.setupWithViewPager(view.viewPagerQuestion)

        return view
    }

}