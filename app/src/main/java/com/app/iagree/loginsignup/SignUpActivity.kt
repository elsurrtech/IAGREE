package com.app.iagree.loginsignup

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.whenCreated
import com.app.iagree.MainActivity
import com.app.iagree.R
import com.app.iagree.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import java.util.*
import kotlin.collections.HashMap

class SignUpActivity : AppCompatActivity() {

    var a:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val username = userName.text.toString()


        btnLogin.setOnClickListener {
            val i = Intent(this,LoginActivity::class.java)
            startActivity(i)
        }


        userName.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchUser(s.toString().toLowerCase())
            }
        })


        btnSignUp.setOnClickListener {

            createAccount()

        }

    }

    private fun searchUser(input:String){
        val query= FirebaseDatabase.getInstance().reference.child("Users")
            .orderByChild("username").equalTo(input)

        val progressBar:ProgressBar = findViewById(R.id.progressbar_signup)
        progressBar.visibility = View.VISIBLE

        query.addValueEventListener(object :ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    btnSignUp.visibility = View.GONE
                    btnSignUp_disable.visibility = View.VISIBLE
                    userName.error = "Username Already Exist"
                    progressBar.visibility = View.INVISIBLE
                    text_input_layout.boxStrokeColor = resources.getColor(R.color.red,null)
                }

                else{
                    btnSignUp.visibility = View.VISIBLE
                    btnSignUp_disable.visibility = View.GONE
                    progressBar.visibility = View.INVISIBLE
                    text_input_layout.boxStrokeColor = resources.getColor(R.color.green,null)
                }



            }

            override fun onCancelled(p0: DatabaseError) {
                userName.error = "Internet Connection not good"
            }
        })
    }

    override fun onBackPressed() {
        val i = Intent(this,LoginActivity::class.java)
        startActivity(i)
    }

    private fun createAccount(){

        val fullName = fullName.text.toString()
        val email = email.text.toString()
        val username = userName.text.toString()
        val password = password.text.toString()

        when{
            TextUtils.isEmpty(fullName) -> Toast.makeText(this,"Full Name Required",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this,"email Required",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(username) -> Toast.makeText(this,"username Required",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this,"password Required",Toast.LENGTH_SHORT).show()

            else->{

                val mAuth:FirebaseAuth=FirebaseAuth.getInstance()

                //progress Dialog
                val progress = ProgressDialog(this)
                progress.setTitle("Sign Up")
                progress.setMessage("Please Wait..")
                progress.setCanceledOnTouchOutside(false)
                progress.show()

                mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener {
                        task ->
                        if (task.isSuccessful){

                            saveUserInfo(fullName,username,email,progress)

                        }else{
                            val message = task.exception!!.toString()
                            Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progress.dismiss()

                        }
                    }

            }

        }

    }


    private fun saveUserInfo(fullname:String, username:String, email:String,progressDialog: ProgressDialog){

        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserId
        userMap["username"] = username.toLowerCase(Locale.ROOT)
        userMap["fullname"] = fullname
        userMap["email"] = email
        userMap["bio"] = "Hey, This is bio Field"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/i-agree-ee3e4.appspot.com/o/Default%20Images%2Fimg2.png?alt=media&token=635aa0e4-6fde-42ed-9ada-046db18c27d0"


        userRef.child(currentUserId).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this,"Account Successfully Created",Toast.LENGTH_LONG).show()

                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(currentUserId)
                        .child("Following").child(currentUserId).setPriority(true)

                    val i  = Intent(this,MainActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                    finish()
                }else{
                    val message = task.exception!!.toString()
                    Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }

            }

    }

}
