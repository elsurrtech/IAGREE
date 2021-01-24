package com.app.iagree.questionare.adaptor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.app.iagree.home.InfoFragment
import com.app.iagree.home.MemesFragment
import com.app.iagree.home.PersonalFragment
import com.app.iagree.questionare.QHomeFragment
import com.app.iagree.questionare.QInternationalFragment
import com.app.iagree.ui.home.HomeFragment

class questionPagesAdaptor(fm:FragmentManager):FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when(position){
            0-> {return QHomeFragment()
            }
            1-> {return QInternationalFragment()
            }

            else -> {return QHomeFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){

            0-> {return "Basic"}
            1-> {return "International"}

        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return 2
    }

}