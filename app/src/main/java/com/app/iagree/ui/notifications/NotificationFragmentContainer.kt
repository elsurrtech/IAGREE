package com.app.iagree.ui.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.iagree.R
import com.app.iagree.adaptor.HomePagesAdaptor
import com.app.iagree.adaptor.NotificationPagesAdaptor
import com.google.android.material.badge.BadgeDrawable
import kotlinx.android.synthetic.main.fragment_home_container.view.*

class NotificationFragmentContainer : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notification_container, container, false)

        view.viewPager.adapter = NotificationPagesAdaptor(childFragmentManager)
        view.viewPager.currentItem = 0
        view.r2.setupWithViewPager(view.viewPager)

        return view
    }

}