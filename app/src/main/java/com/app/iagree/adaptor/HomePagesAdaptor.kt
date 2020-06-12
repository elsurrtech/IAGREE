package com.app.iagree.adaptor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.app.iagree.home.InfoFragment
import com.app.iagree.home.MemesFragment
import com.app.iagree.home.PersonalFragment
import com.app.iagree.ui.home.HomeFragment

class HomePagesAdaptor(fm:FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
            when(position){
                0-> {return HomeFragment()}
                1-> {return PersonalFragment()}
                2-> {return MemesFragment()}
                3-> {return InfoFragment()}

                else -> {return HomeFragment()}
            }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){

            0-> {return "All"}
            1-> {return "People"}
            2-> {return "Memes"}
            3-> {return "Info"}

        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return 4
    }
}