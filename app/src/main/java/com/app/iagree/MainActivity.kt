package com.app.iagree

import android.app.Notification
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.translationMatrix
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.app.iagree.ui.add.AddFragment
import com.app.iagree.ui.dashboard.DashboardFragment
import com.app.iagree.ui.home.HomeFragment
import com.app.iagree.ui.homepages.HomeFragmentContainer
import com.app.iagree.ui.notifications.NotificationFragmentContainer
import com.app.iagree.ui.search.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var navController: NavController? = null

    private var fragment1 = HomeFragmentContainer()
    private var fragment2 = DashboardFragment()
    private var fragment3 = SearchFragment()
    private var fragment4 = AddFragment()
    private var fragment5 = NotificationFragmentContainer()

    var active: Fragment = fragment1

    private var fm : FragmentManager = supportFragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

//        fm.beginTransaction().add(R.id.nav_host_fragment,fragment3,"3").hide(fragment3).commit()
//        fm.beginTransaction().add(R.id.nav_host_fragment,fragment2,"2").hide(fragment2).commit()
//        fm.beginTransaction().add(R.id.nav_host_fragment,fragment4,"4").hide(fragment4).commit()
//        fm.beginTransaction().add(R.id.nav_host_fragment,fragment5,"5").hide(fragment5).commit()
//        fm.beginTransaction().add(R.id.nav_host_fragment,fragment1,"1").commit()

        //navController = findNavController(R.id.nav_host_fragment)
        //navView.setupWithNavController(navController!!)
        navView.selectedItemId=R.id.navigation_home

    }

    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.navigation_home-> {


                        /* bro agar m active use krta hu tab scene ye hai ki same element p click kiya jai toh home khul rha hai kyuki active
                        hide ho ja ra hai isliye mne active hta diya and on create m hamesha main load hoga selecteditem = navigation home se
                        baki uper ka code kisi kaam ka nhi isliye comment kr diya

                        */


                        fm.beginTransaction().replace(R.id.nav_host_fragment,fragment1).commit()
                      //  fm.beginTransaction().hide(active).show(fragment1).commit()
//                        active = fragment1
                        return true
                    }

                    R.id.navigation_dashboard-> {
                        fm.beginTransaction().replace(R.id.nav_host_fragment,fragment2).hide(fragment1).commit()

                      //  fm.beginTransaction().hide(active).show(fragment2).commit()
//                        active = fragment2
                        return true
                    }

                    R.id.navigation_search-> {
                        fm.beginTransaction().replace(R.id.nav_host_fragment,fragment3).hide(fragment1).commit()
                        //fm.beginTransaction().hide(active).show(fragment3).commit()
//                        active = fragment3
                        return true
                    }

                    R.id.navigation_add-> {
                        fm.beginTransaction().replace(R.id.nav_host_fragment,fragment4).hide(fragment1).commit()
                        //fm.beginTransaction().hide(active).show(fragment4).commit()
                     //   active = fragment4
                        return true
                    }

                    R.id.navigation_notifications-> {
                        fm.beginTransaction().replace(R.id.nav_host_fragment,fragment5).hide(fragment1).commit()
                        //fm.beginTransaction().hide(active).show(fragment5).commit()
                      //  active = fragment5
                        return true
                    }
                }
                return false
            }

        }

    override fun onSupportNavigateUp(): Boolean {
        return navController!!.navigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

}
