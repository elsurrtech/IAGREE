package com.app.iagree.questionare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.iagree.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_question.*

class QuestionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        btnBack_q_home.setOnClickListener {
            onBackPressed()
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container,QPagesFragment()).commit()
        }

        textView_question_activity.text = "Latest Questions"

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){

                R.id.navigation_home->{
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragment_container,QPagesFragment()).commit()
                    }

                    textView_question_activity.text = "Latest Questions"
                }

                R.id.navigation_profile-> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragment_container,QQuestionsFragment()).hide(QHomeFragment()).commit()
                    }

                    textView_question_activity.text = "Your Questions"
                }

            }

            true
        }

    }

    fun currentFrag(f: Fragment,s:String){

    }
}