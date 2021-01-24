package com.app.iagree.questionare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast
import com.app.iagree.R
import com.app.iagree.model.User
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_question.*
import kotlinx.android.synthetic.main.add_fragment.*
import kotlinx.android.synthetic.main.bottomsheet_add_question.*
import kotlinx.android.synthetic.main.bottomsheet_add_question.view.*
import kotlinx.android.synthetic.main.bottomsheet_add_question.view.anonymous_checked
import kotlinx.android.synthetic.main.bottomsheet_add_question.view.username_btmsheet_add_question
import kotlinx.android.synthetic.main.bottomsheet_add_question2.view.*
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap


class AddQuestionActivity : AppCompatActivity() {

    var userName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_question)

        val bottomSheetDialog = BottomSheetDialog(this)
        val v = layoutInflater.inflate(R.layout.bottomsheet_add_question,null)
        bottomSheetDialog.setContentView(v)

        val bottomSheetDialog2 = BottomSheetDialog(this)
        val v2 = layoutInflater.inflate(R.layout.bottomsheet_add_question2,null)
        bottomSheetDialog2.setContentView(v2)

        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val x = p0.getValue(User::class.java)
                username_add_question.text = x!!.getFullname()
                v.username_btmsheet_add_question?.text = x.getFullname()
                userName = x.getUsername()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        btnChoose_anonymous.setOnClickListener {
            bottomSheetDialog.show()
        }

        btnChoose_questionType.setOnClickListener{
            bottomSheetDialog2.show()
        }

        v.r1_add_question.setOnClickListener {
            v.username_checked?.visibility = View.VISIBLE
            v.anonymous_checked?.visibility = View.GONE
            username_add_question.text = v.username_btmsheet_add_question.text
            bottomSheetDialog.dismiss()
        }

        v.r2_add_question.setOnClickListener {
            v.username_checked?.visibility = View.GONE
            v.anonymous_checked?.visibility = View.VISIBLE
            username_add_question.text = "Anonymous"
            bottomSheetDialog.dismiss()
        }

        v2.r1_1_add_question.setOnClickListener {
            v2.username_checked2?.visibility = View.VISIBLE
            v2.anonymous_checked2?.visibility = View.GONE
            questionType_add_question.text = "National Students"
            bottomSheetDialog2.dismiss()
        }

        v2.r2_2_add_question.setOnClickListener {
            v2.username_checked2?.visibility = View.GONE
            v2.anonymous_checked2?.visibility = View.VISIBLE
            questionType_add_question.text = "International Students"
            bottomSheetDialog2.dismiss()
        }

        btnPost_question?.setOnClickListener {
            if (et_question.text.toString() == ""){
                Toast.makeText(this,"Question is Empty",Toast.LENGTH_SHORT).show()
            }else if (et_question.text.length < 10){
                Toast.makeText(this,"Question Length must be greater than 10",Toast.LENGTH_SHORT).show()
            }else{
                are_you_sure?.visibility = View.VISIBLE
                btnPost_question?.visibility = View.GONE
            }

        }

        btnNo_question?.setOnClickListener {
            are_you_sure?.visibility = View.GONE
            btnPost_question?.visibility = View.VISIBLE
        }

        btnYes_question?.setOnClickListener {
            postQuestion()
        }

    }

    private fun postQuestion(){
        val ref = FirebaseDatabase.getInstance().reference.child("Questions")
        val key = ref.push().key

        val calender = Calendar.getInstance()
        val currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calender.time)

        val quesMap = HashMap<String,Any>()
        quesMap["key"] = key!!
        quesMap["name"] = username_add_question.text.toString()
        quesMap["type"] = questionType_add_question.text.toString()
        quesMap["username"] = userName.toString()
        quesMap["question"] = et_question.text.toString()
        quesMap["description"] = et_description.text.toString()
        quesMap["uid"] = FirebaseAuth.getInstance().currentUser!!.uid.toString()
        quesMap["date"] = currentDate.toString()
        ref.child(key!!).updateChildren(quesMap).addOnCompleteListener {
            if (it.isComplete){
                Toast.makeText(this,"Question Posted",Toast.LENGTH_LONG).show()
                onBackPressed()
            }
        }
    }
}