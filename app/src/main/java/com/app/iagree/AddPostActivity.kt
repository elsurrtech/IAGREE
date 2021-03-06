package com.app.iagree

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class AddPostActivity : AppCompatActivity() {

    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference? = null

    private var postCategory = "Post"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Post Pictures")

        share_post_btn_addPost_activity.setOnClickListener {
            uploadImage()
        }

        CropImage.activity()
            .start(this)

        image_add_post_activity.setOnClickListener {
            CropImage.activity()
                .start(this)
        }
        btnBack.setOnClickListener {
            onBackPressed()
        }

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener{group, checkedId ->

            when(checkedId){
                R.id.personal->{
                    postCategory = "Personal"
                }
                R.id.meme->{
                    postCategory = "Meme"
                }
                R.id.informational->{
                    postCategory = "Informational"
                }
            }

        }


    }

    private fun uploadImage(){

        when{
            editText_add_post_activity.text.toString() == "" -> Toast.makeText(this,"Enter Bio Details", Toast.LENGTH_SHORT).show()
            imageUri == null -> Toast.makeText(this,"Please Select Image", Toast.LENGTH_SHORT).show()

            else ->{

                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Sharing New Post")
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


                        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                        val postID = ref.push().key

                        val calender = Calendar.getInstance()
                        val currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calender.time)

                        val postMap = HashMap<String, Any>()
                        postMap["postID"] = postID!!
                        postMap["date"] = currentDate.toString()
                        postMap["description"] = editText_add_post_activity.text.toString()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postImage"] = myUrl
                        postMap["postCategory"] = postCategory

                        ref.child(postID).updateChildren(postMap)

                        Toast.makeText(this,"Post Shared",Toast.LENGTH_LONG).show()

                        val i  = Intent(this,MainActivity::class.java)
                        startActivity(i)
                        finish()
                        progressDialog.dismiss()
                    }
                    else{
                        progressDialog.dismiss()
                    }

                })
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data!=null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            image_add_post_activity.setImageURI(imageUri)
        }else{
            onBackPressed()
        }
    }

    private fun online(){
        val ref = FirebaseDatabase.getInstance().reference.child("OnlineUsers")
        ref.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue("true")
    }


}