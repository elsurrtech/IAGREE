package com.app.iagree.adaptor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.app.iagree.home.InfoFragment
import com.app.iagree.home.MemesFragment
import com.app.iagree.home.PersonalFragment
import com.app.iagree.ui.home.HomeFragment
import com.app.iagree.ui.notifications.NotificationsFragment
import com.app.iagree.ui.notifications.RequestFragment

class NotificationPagesAdaptor(fm:FragmentManager): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when(position){
            0-> {return NotificationsFragment()
            }
            1-> {return RequestFragment()
            }
            else -> {return NotificationsFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){

            0-> {return "All"}
            1-> {return "Requests"}

        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return 2
    }
}