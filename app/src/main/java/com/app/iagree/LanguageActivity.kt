package com.app.iagree

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.app.iagree.loginsignup.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_language.*

class LanguageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        var currentLanguage = ""

        val english = findViewById<RelativeLayout>(R.id.english)
        val latvian = findViewById<RelativeLayout>(R.id.latvian)
        val russian  = findViewById<RelativeLayout>(R.id.russian)
        val btnLanguage = findViewById<Button>(R.id.btnLanguage)

        btnLanguage?.visibility = View.INVISIBLE

        english.setOnClickListener {
            english.background = resources.getDrawable(R.drawable.background)
            latvian.background = resources.getDrawable(R.drawable.background_btmsheet_et)
            russian.background = resources.getDrawable(R.drawable.background_btmsheet_et)
            textEnglish?.setTextColor(resources.getColor(R.color.black))
            textLatvian?.setTextColor(resources.getColor(R.color.white))
            textRussian?.setTextColor(resources.getColor(R.color.white))
            btnLanguage?.visibility = View.VISIBLE
            btnLanguage?.text = "ok"
            currentLanguage = "english"
        }

        latvian.setOnClickListener {
            english.background = resources.getDrawable(R.drawable.background_btmsheet_et)
            latvian.background = resources.getDrawable(R.drawable.background)
            russian.background = resources.getDrawable(R.drawable.background_btmsheet_et)
            textLatvian?.setTextColor(resources.getColor(R.color.black))
            textEnglish?.setTextColor(resources.getColor(R.color.white))
            textRussian?.setTextColor(resources.getColor(R.color.white))
            btnLanguage?.visibility = View.VISIBLE
            btnLanguage?.text = "labi"
            currentLanguage = "latvian"
        }

        russian.setOnClickListener {
            english.background = resources.getDrawable(R.drawable.background_btmsheet_et)
            latvian.background = resources.getDrawable(R.drawable.background_btmsheet_et)
            russian.background = resources.getDrawable(R.drawable.background)
            textEnglish?.setTextColor(resources.getColor(R.color.white))
            textLatvian?.setTextColor(resources.getColor(R.color.white))
            textRussian?.setTextColor(resources.getColor(R.color.black))
            btnLanguage?.visibility = View.VISIBLE
            btnLanguage?.text = "хорошо"
            currentLanguage = "russian"
        }

        btnLanguage?.setOnClickListener {
            if(TextUtils.isEmpty(btnLanguage.toString())){
                Toast.makeText(this,"select a language / izvēlieties valodu",Toast.LENGTH_LONG).show()
            }else{
                SaveLanguage(this).setLanguage(currentLanguage)
                if (FirebaseAuth.getInstance().currentUser == null){
                    startActivity(Intent(this,LoginActivity::class.java))
                    finish()
                }
                else{
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }


            }
        }

    }
}