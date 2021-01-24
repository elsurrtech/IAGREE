package com.app.iagree.adaptor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.app.iagree.home.InfoFragment
import com.app.iagree.home.MemesFragment
import com.app.iagree.home.PersonalFragment
import com.app.iagree.messages.ConfessionsFragment
import com.app.iagree.messages.MessagesFragment
import com.app.iagree.model.message
import com.app.iagree.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessagePagesAdaptor(fm: FragmentManager): FragmentPagerAdapter(fm)  {



        override fun getItem(position: Int): Fragment {

        when(position){
            0-> { return MessagesFragment()
            }
            1-> {return ConfessionsFragment()
            }

            else -> {return MessagesFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){

            0-> { return "Messages"}

            1-> {return "Confessions"}

        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return 2
    }

}