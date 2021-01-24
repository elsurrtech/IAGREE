package com.app.iagree

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_phone_auth.*
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : AppCompatActivity() {

    lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var mAuth: FirebaseAuth
    var verificationId = ""
    var phone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)

        mAuth = FirebaseAuth.getInstance()

        i1_phoneAuth.setOnClickListener {
            onBackPressed()
            finish()
        }

        sendOtp?.setOnClickListener {
            progressbar_phoneAuth?.visibility = View.VISIBLE
            progressbar_phoneAuth?.startAnimation(AnimationUtils.loadAnimation(this,R.anim.animdetailsentryup))
            verify()
        }

        btnPhoneAuth.setOnClickListener {
            authenticate()
        }


    }

    private fun signIn (credential: PhoneAuthCredential) {
        FirebaseDatabase.getInstance().reference.child("Users").child(mAuth.currentUser!!.uid)
            .child("phone").setValue(phone).addOnCompleteListener {
                if (it.isSuccessful){
                    FirebaseDatabase.getInstance().reference.child("phoneVerified")
                        .child(mAuth.currentUser!!.uid).child("phone").setValue(phone).addOnCompleteListener {task->
                            if (task.isSuccessful){
                                toast("Verified")
                                onBackPressed()
                            }
                        }
                }
            }
    }

    private fun verificationCallbacks () {
        mCallbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signIn(credential)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(this@PhoneAuthActivity,"Verification Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationId = p0.toString()
                progressbar_phoneAuth?.visibility = View.GONE
                LL_otp_phoneAuth?.visibility = View.VISIBLE
                LL_otp_phoneAuth.startAnimation(AnimationUtils.loadAnimation(this@PhoneAuthActivity,R.anim.animdetails))
            }

        }
    }

    private fun verify() {

        verificationCallbacks()

        val phnNo ="+371"+ phone_add_fragment.text.toString()
        phone = phnNo

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phnNo,
            60,
            TimeUnit.SECONDS,
            this,
            mCallbacks
        )
    }

    private fun authenticate () {

        val verifiNo = otp_phone_auth.text.toString()

        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, verifiNo)

        signIn(credential)

    }

    private fun toast (msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}