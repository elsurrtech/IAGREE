package com.app.iagree.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.widget.Button
import android.widget.Switch
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import com.app.iagree.R
import com.app.iagree.loginsignup.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_settings.*
import java.net.Inet4Address
import java.util.zip.Inflater

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val i = Intent(this,LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()
        }

        val toggleButton = findViewById<Switch>(R.id.toggleButton_accountPrivacy_settingsActivity)
        toggleButton.setOnClickListener {
            if (toggleButton.isChecked){
                showAlertAccountPrivacy(toggleButton)
            }else{
                FirebaseDatabase.getInstance().reference
                    .child("PrivateAccounts").child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .removeValue()
            }
        }

        userPoll.setOnClickListener {
            startActivity(Intent(this,PollActivity::class.java))
        }

        val ref = FirebaseDatabase.getInstance().reference
            .child("PrivateAccounts").child(FirebaseAuth.getInstance().currentUser!!.uid)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    toggleButton.isChecked = true
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    fun showAlertAccountPrivacy(t:Switch){
        val inflater = layoutInflater
        val inflate_view = inflater.inflate(R.layout.alertdialog_accountprivacy, null)
        val yes = inflate_view.findViewById<Button>(R.id.btn_yes_accountPrivacy)
        val no = inflate_view.findViewById<Button>(R.id.btn_no_accountPrivacy)

        val alert = AlertDialog.Builder(this)
        alert.setView(inflate_view)
        alert.setCancelable(false)
        val x =alert.create()
        x.show()
        yes.setOnClickListener {
            FirebaseDatabase.getInstance().reference
                .child("PrivateAccounts").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(true)

            t.isChecked = true
            x.cancel()
        }
        no.setOnClickListener {
            x.cancel()
            t.isChecked = false
        }




    }
}
