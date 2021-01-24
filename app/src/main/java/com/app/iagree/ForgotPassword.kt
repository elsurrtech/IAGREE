package com.app.iagree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_login.*

class ForgotPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        backToLogin.setOnClickListener {
            onBackPressed()
        }





        btnSendVerification.setOnClickListener {
            sendMail()
        }
    }

   private fun sendMail(){

       val email = email_password.text.toString()

       if (email == ""){
           Toast.makeText(this,"Please Enter Email",Toast.LENGTH_LONG).show()
       }else{
           FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
               if (it.isSuccessful){
                   Toast.makeText(this,"Verification Email Sent!",Toast.LENGTH_LONG).show()
                   onBackPressed()
               }else{
                   Toast.makeText(this,"Please Enter a Valid Email",Toast.LENGTH_LONG).show()
               }
           }
       }

    }
}