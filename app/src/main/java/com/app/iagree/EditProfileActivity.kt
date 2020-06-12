package com.app.iagree

import android.app.Activity
import android.app.ProgressDialog
import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.app.iagree.model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
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
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import java.net.NetworkInterface
import java.util.*
import kotlin.collections.HashMap

class EditProfileActivity : AppCompatActivity() {

    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null
    private lateinit var firebaseUser: FirebaseUser

    var connectivity: ConnectivityManager? = null
    var info : NetworkInfo? = null

    var check = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        userInfo()

        text_change_image_edit_profile.setOnClickListener {
            checker = "clicked"
            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this)
        }

        username_edit_profile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchUser(s.toString().toLowerCase())
            }
        })



        update_infoBtn_edit_profile.setOnClickListener {
            if (isConnected()){
                if (checker == "clicked"){

                    uploadImageAndUpdateInfo()

                }else{
                    updateUserInfo()
                }
            }else{
                Toast.makeText(this,"Check Internet Connection",Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data!=null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            image_edit_profile.setImageURI(imageUri)
        }
    }

    private fun uploadImageAndUpdateInfo(){

        when{

            fullName_edit_profile.text.toString() == "" -> Toast.makeText(this,"Enter Full Name",Toast.LENGTH_SHORT).show()
            username_edit_profile.text.toString() == "" -> Toast.makeText(this,"Enter a username",Toast.LENGTH_SHORT).show()
            bio_edit_profile.text.toString() == "" -> Toast.makeText(this,"Enter Bio Details",Toast.LENGTH_SHORT).show()
            imageUri == null -> Toast.makeText(this,"Please Select Image",Toast.LENGTH_SHORT).show()

            else->{

                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Account Settings")
                progressDialog.setMessage("Please Wait")
                progressDialog.show()

                val fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot,Task<Uri>>{task ->
                    if (!task.isSuccessful){
                        task.exception.let {
                            throw it!!
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener (OnCompleteListener<Uri>{task ->

                    if (task.isSuccessful){
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")
                        val userMap = HashMap<String, Any>()
                        userMap["username"] = username_edit_profile.text.toString().toLowerCase(Locale.ROOT)
                        userMap["fullname"] = fullName_edit_profile.text.toString()
                        userMap["bio"] = bio_edit_profile.text.toString()
                        userMap["image"] = myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap).addOnCompleteListener { task ->

                            if (task.isSuccessful){
                                finish()
                                onBackPressed()
                                Toast.makeText(this,"Profile Updated",Toast.LENGTH_LONG).show()
                            }

                        }


                    }
                    else{
                        progressDialog.dismiss()
                    }

                })
            }
        }
    }

    private fun userInfo(){
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){

                    val user = p0.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.ic_account_circle_black_24dp).into(image_edit_profile)
                    username_edit_profile.setText(user!!.getUsername())
                    fullName_edit_profile.setText(user!!.getFullname())
                    bio_edit_profile.setText(user!!.getBio())

                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })
    }

    private fun updateUserInfo(){

        if (fullName_edit_profile.text.toString() == ""){
            Toast.makeText(this,"Enter Full Name",Toast.LENGTH_SHORT).show()
        }else if (username_edit_profile.text.toString() == ""){
            Toast.makeText(this,"Enter a username",Toast.LENGTH_SHORT).show()

        }else if (bio_edit_profile.text.toString() == ""){
            Toast.makeText(this,"Enter Bio Details",Toast.LENGTH_SHORT).show()
        }else{
            val usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
            val userMap = HashMap<String, Any>()
            userMap["username"] = username_edit_profile.text.toString().toLowerCase(Locale.ROOT)
            userMap["fullname"] = fullName_edit_profile.text.toString()
            userMap["bio"] = bio_edit_profile.text.toString()

            usersRef.child(firebaseUser.uid).updateChildren(userMap)

            Toast.makeText(this,"Profile Updated",Toast.LENGTH_LONG).show()

            val i  = Intent(this,MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()
        }


    }

    fun isConnected(): Boolean{

        connectivity = this.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity!= null){
            info = connectivity!!.activeNetworkInfo
            if (info!= null){
                if (info!!.state == NetworkInfo.State.CONNECTED){
                    return true
                }
            }
        }


        return false
    }

    private fun searchUser(input:String){
        val query= FirebaseDatabase.getInstance().reference.child("Users")
            .orderByChild("username").equalTo(input)

        val progressBar: ProgressBar = findViewById(R.id.progressbar_editProfile)
        progressBar.visibility = View.VISIBLE

        query.addValueEventListener(object :ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){

                    val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)
                    usersRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(datasnapshot: DataSnapshot) {

                            if (datasnapshot.exists()){

                                val user = datasnapshot.getValue<User>(User::class.java)
                                if (input == user!!.getUsername()){
                                    progressBar.visibility = View.INVISIBLE
                                    text_input_layout_editProfile.boxStrokeColor = resources.getColor(R.color.white,null)
                                }else{
                                    username_edit_profile.error = "Username Already Exist"
                                    progressBar.visibility = View.INVISIBLE
                                    text_input_layout_editProfile.boxStrokeColor = resources.getColor(R.color.red,null)
                                }


                            }
                        }

                        override fun onCancelled(datasnapshot: DatabaseError) {


                        }
                    })



                }

                else{
                    update_infoBtn_edit_profile.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                    text_input_layout_editProfile.boxStrokeColor = resources.getColor(R.color.green,null)
                }



            }

            override fun onCancelled(p0: DatabaseError) {
                userName.error = "Internet Connection not good"
            }
        })
    }
}
