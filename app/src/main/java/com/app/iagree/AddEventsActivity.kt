package com.app.iagree

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_events.*
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.bottomsheet_add_event.*
import kotlinx.android.synthetic.main.bottomsheet_add_event.view.*
import kotlinx.android.synthetic.main.bottomsheet_add_event.view.amount_bs_add_event
import kotlinx.android.synthetic.main.bottomsheet_add_event_location.*
import kotlinx.android.synthetic.main.bottomsheet_add_event_location.view.*
import kotlinx.android.synthetic.main.bottomsheet_add_event_location.view.checked_online_bs_add_event
import kotlinx.coroutines.CancellationException
import java.text.DateFormat
import java.util.*

class AddEventsActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference? = null

    private var firebaseUser : FirebaseUser? = null

    //date
    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_events)

       firebaseUser = FirebaseAuth.getInstance().currentUser!!
       storagePostPicRef = FirebaseStorage.getInstance().reference.child("Event Pictures")

        //contact number
        getContactNumber()

        //photo
        addPhoto()

        //date
        pickDate()

        //entry fees
        selectEntryFees()

        //get Address
        selectAddress()

        //post
        postEvent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data!=null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            imageView_add_events?.visibility = View.VISIBLE
            btnCancel_add_event?.visibility = View.VISIBLE
            imageView_add_events?.setImageURI(imageUri)
        }
    }

    private fun addPhoto(){
        btnAddPhoto_add_event?.setOnClickListener {
            CropImage.activity()
                .start(this)
        }

        imageView_add_events?.setOnClickListener {
            CropImage.activity()
                .start(this)
        }

        btnCancel_add_event?.setOnClickListener {
            imageView_add_events?.visibility = View.GONE
            btnCancel_add_event?.visibility = View.GONE
        }
    }

    private fun postEvent(){
        btnPost_add_event?.setOnClickListener {

            if (TextUtils.isEmpty(title_add_event!!.text.toString()) || title_add_event!!.text.length < 6){
                toast("Title must be 6 character or more")
            }
            else if (date_text_add_event.text.toString() == "Date and Time" || time_text_add_event.text.toString() == ""){
                toast("Please Enter Date and Time")
            }
            else if(location_text_add_event.text.toString() == "Location"){
                toast("Specify Online or Offline Event")
            }
            else if (TextUtils.isEmpty(description_add_event!!.text.toString()) || description_add_event!!.text.length < 10){
                toast("Description must be greater than 10 character")
            }
            else if (amount_add_question.text.toString() == "Select Entry Fees"){
                toast("Select Entry fees")
            }
            else if (TextUtils.isEmpty(members_add_event!!.text.toString()) || members_add_event!!.text.toString() == "0" || members_add_event!!.text.toString() == "1" || members_add_event!!.text.toString() == "2"){
                toast("At Least 3 or more members allowed for event")
            }
            else{

                if (imageView_add_events?.visibility == View.VISIBLE){
                    postEventWithPhoto()
                }else{
                    postEventWithoutPhoto()
                }
            }
        }
    }

    private fun getContactNumber(){
        val x = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        x.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val y = p0.child("phone").value.toString()
                phone_add_event?.text = y

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun postEventWithoutPhoto(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Sharing Event")
        progressDialog.setMessage("Please Wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()


        val ref = FirebaseDatabase.getInstance().reference.child("Events")
        val eventID = ref.push().key

        val calender = Calendar.getInstance()
        val currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calender.time)

        val postMap = HashMap<String, Any>()
        postMap["eventID"] = eventID!!
        postMap["title"] = title_add_event.text.toString()
        postMap["date"] = currentDate.toString()
        postMap["eventDate"] = date_text_add_event.text.toString()
        postMap["eventTime"] = time_text_add_event?.text.toString()
        postMap["address"] = location_text_add_event.text.toString()
        postMap["description"] = description_add_event.text.toString()
        postMap["fees"] = amount_add_question.text.toString()
        postMap["maxPeople"] = members_add_event.text.toString()
        postMap["phone"]= phone_add_event.text.toString()
        postMap["publisher"] = firebaseUser!!.uid
        postMap["postImage"] = ""
        postMap["link"]=""

        ref.child(eventID).updateChildren(postMap).addOnCompleteListener {
            if (it.isSuccessful){
                toast("Event Shared")
                val i  = Intent(this,MainActivity::class.java)
                startActivity(i)
                finish()
                progressDialog.dismiss()
            }
            else{
                toast("Error: Try Again")
                progressDialog.dismiss()
            }
        }

    }

    private fun postEventWithPhoto(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Sharing Event")
        progressDialog.setMessage("Please Wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val fileRef = storagePostPicRef!!.child(System.currentTimeMillis().toString()+".jpg")

        var uploadTask: StorageTask<*>
        uploadTask = fileRef.putFile(imageUri!!)

        uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
            if (!task.isSuccessful){
                task.exception.let {
                    throw it!!
                    progressDialog.dismiss()
                }
            }
            return@Continuation fileRef.downloadUrl
        }).addOnCompleteListener (OnCompleteListener<Uri>{ task ->

            if (task.isSuccessful){
                val downloadUrl = task.result
                myUrl = downloadUrl.toString()


                val ref = FirebaseDatabase.getInstance().reference.child("Events")
                val eventID = ref.push().key

                val calender = Calendar.getInstance()
                val currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calender.time)

                val postMap = HashMap<String, Any>()
                postMap["eventID"] = eventID!!
                postMap["title"] = title_add_event.text.toString()
                postMap["date"] = currentDate.toString()
                postMap["eventDate"] = date_text_add_event.text.toString()
                postMap["eventTime"] = time_text_add_event?.text.toString()
                postMap["address"] = location_text_add_event.text.toString()
                postMap["description"] = description_add_event.text.toString()
                postMap["fees"] = amount_add_question.text.toString()
                postMap["maxPeople"] = members_add_event.text.toString()
                postMap["phone"]= phone_add_event.text.toString()
                postMap["publisher"] = firebaseUser!!.uid
                postMap["postImage"] = myUrl
                postMap["link"]=""

                ref.child(eventID).updateChildren(postMap)

                Toast.makeText(this,"Event Shared",Toast.LENGTH_LONG).show()

                val i  = Intent(this,MainActivity::class.java)
                startActivity(i)
                finish()
                progressDialog.dismiss()
            }
            else{
                progressDialog.dismiss()
                toast("Error: Try Again")
            }

        })
    }

    private fun selectEntryFees(){
        val bs = BottomSheetDialog(this)
        val v = layoutInflater.inflate(R.layout.bottomsheet_add_event, null)
        bs.setContentView(v)

        entryFees_add_event?.setOnClickListener {
            bs.show()
        }

        v.free_bs_add_event?.setOnClickListener {
            v.checked_bs_add_event?.visibility = View.VISIBLE
            amount_add_question?.text = "Free"
            bs.dismiss()
        }

        v.select_amount_bs_add_event?.setOnClickListener {
            if (v.amount_bs_add_event!!.text.toString()== ""){
                Toast.makeText(this,"Enter Amount", Toast.LENGTH_SHORT).show()
            }
            else{
                v.checked_bs_add_event?.visibility = View.GONE
                amount_add_question?.text = v.amount_bs_add_event!!.text.toString() + "€"
                v.amount_bs_add_event!!.text!!.clear()
                bs.dismiss()
            }
        }

    }

    private fun selectAddress(){
        val bs = BottomSheetDialog(this)
        val v = layoutInflater.inflate(R.layout.bottomsheet_add_event_location, null)
        bs.setContentView(v)

        location_add_event?.setOnClickListener {
            bs.show()
        }

        v.online_bs_add_event?.setOnClickListener {
            v.checked_online_bs_add_event?.visibility = View.VISIBLE
            location_text_add_event?.text = "Online Event"
            bs.dismiss()
        }

        v.select_address_bs_add_event?.setOnClickListener {
            if (v.address_bs_add_event?.text.toString() == ""){
                Toast.makeText(this,"Enter Location", Toast.LENGTH_SHORT).show()
            }else{
                v.checked_online_bs_add_event?.visibility = View.GONE
                location_text_add_event?.text = v.address_bs_add_event?.text.toString()
                v.address_bs_add_event?.text!!.clear()
                bs.dismiss()
            }
        }
    }

    fun pickDate(){
        date_add_event?.setOnClickListener {
            getDateTimeCalender()

            DatePickerDialog(this,this,year,month,day).show()
        }
    }

    private fun getDateTimeCalender(){
        val cal = Calendar.getInstance()
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        day = cal.get(Calendar.DAY_OF_MONTH)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        getDateTimeCalender()

        TimePickerDialog(this,this,hour,minute,false).show()

    }

    fun toast(x:String){
        Toast.makeText(this,x,Toast.LENGTH_SHORT).show()

    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
        var x = ""

        if (minute == 0){
            x = "00"
        }
        else if (minute == 1){
            x = "01"
        }
        else if (minute == 2){
            x = "02"
        }
        else if (minute == 3){
            x = "03"
        }
        else if (minute == 4){
            x = "04"
        }
        else if (minute == 5){
            x = "05"
        }
        else if (minute == 6){
            x = "06"
        }
        else if (minute == 7){
            x = "07"
        }
        else if (minute == 8){
            x = "08"
        }
        else if (minute == 9){
            x = "09"
        }
        else
            x = minute.toString()



        //yyyy-mm-dd
        date_text_add_event?.text = "$savedYear-$savedMonth-$savedDay"
        time_text_add_event?.text = "$savedHour:$x"
        comma_text_add_event?.visibility = View.VISIBLE
    }
}