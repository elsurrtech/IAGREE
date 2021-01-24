package com.app.iagree

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.app.iagree.loginsignup.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val SPLASH_TIME_OUT = 300
        val homeIntent = Intent(this@SplashActivity, LoginActivity::class.java)
        Handler().postDelayed({
            //Do some stuff here, like implement deep linking
            if (SaveLanguage(this).loadLanguage() == "" || SaveLanguage(this).loadLanguage()!!.isEmpty()){
                startActivity(Intent(this,LanguageActivity::class.java))
                finish()
            }else{
                startActivity(homeIntent)
                finish()
            }

        }, SPLASH_TIME_OUT.toLong())

    }
}