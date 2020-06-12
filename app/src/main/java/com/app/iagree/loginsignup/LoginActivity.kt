package com.app.iagree.loginsignup

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.app.iagree.MainActivity
import com.app.iagree.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
            val i = Intent(this,SignUpActivity::class.java)
            startActivity(i)
        }

        btnLogin.setOnClickListener {
            loginUser()
        }


    }

    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser!=null){
            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
            finish()
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

                        val i = Intent(this,MainActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(i)
                        finish()
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
}
