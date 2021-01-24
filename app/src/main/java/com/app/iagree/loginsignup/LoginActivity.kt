package com.app.iagree.loginsignup

import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.iagree.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SaveLanguage(this).loadLanguage() == "english"){

            setApplicationLocale("en")

        }else if (SaveLanguage(this).loadLanguage() == "russian"){

            setApplicationLocale("ru")

        }else if (SaveLanguage(this).loadLanguage() == "latvian")
            setApplicationLocale("lv")
        setContentView(R.layout.activity_login)

        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
            val i = Intent(this,SignUpActivity::class.java)
            startActivity(i)
        }

        btnLogin.setOnClickListener {
            loginUser()
        }

        forgotPassword.setOnClickListener {
            startActivity(Intent(this,ForgotPassword::class.java))
        }


    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser!=null ) {
            if (FirebaseAuth.getInstance().currentUser!!.isEmailVerified){
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                finish()
            }

        }
    }

    private fun loginUser(){

        val email = email.text.toString()
        val password = passeord.text.toString()

        when{
            TextUtils.isEmpty(email)-> Toast.makeText(this,"Enter Username",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password)-> Toast.makeText(this,"Enter Password",Toast.LENGTH_SHORT).show()

            else ->{

                val progress = ProgressDialog(this)
                progress.setTitle("Signing In")
                progress.setCanceledOnTouchOutside(false)
                progress.show()

                val mAuth:FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {task ->
                    if(task.isSuccessful){
                        progress.dismiss()

                        if (mAuth.currentUser!!.isEmailVerified){
                           val x = SaveLanguage(this).loadCollege()
                                val i = Intent(this,MainActivity::class.java)
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(i)
                                finish()
                        }else{
                            Toast.makeText(this,"Verify Email",Toast.LENGTH_SHORT).show()
                        }


                    }else{
                        val m =task.exception.toString()
                        Toast.makeText(this,"Error: $m",Toast.LENGTH_SHORT).show()
                        FirebaseAuth.getInstance().signOut()
                        progress.dismiss()
                    }
                }

            }

        }

    }

    override fun onBackPressed() {
        finishAffinity()
    }

    private fun setApplicationLocale(locale: String) {
        val resources: Resources = resources
        val dm: DisplayMetrics = resources.getDisplayMetrics()
        val config: Configuration = resources.getConfiguration()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(Locale(locale.toLowerCase()))
        } else {
            config.locale = Locale(locale.toLowerCase())
        }
        resources.updateConfiguration(config, dm)
    }

}
