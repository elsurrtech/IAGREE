package com.app.iagree.messages

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.Notifications.*
import com.app.iagree.R
import com.app.iagree.adaptor.MessageAdaptor
import com.app.iagree.model.User
import com.app.iagree.model.message
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_chat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {

    var receiverUserID : String = ""
    var firebaseUser :FirebaseUser? = null

    var messageAdapter: MessageAdaptor? = null
    var messageList: MutableList<message>? = null

    var recyclerView : RecyclerView? = null

    //notifications
    var notify = false
    var apiService: APIService? = null

    var usersRef : DatabaseReference?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        apiService=Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        receiverUserID = intent.getStringExtra("receiverID")

        firebaseUser = FirebaseAuth.getInstance().currentUser

        userInfo(image_chat_activity,username_chat_activity,receiverUserID)

        recyclerView = findViewById(R.id.recyclerView_chat_activity)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.setItemViewCacheSize(8)



        ic_send_chat_activity.setOnClickListener {
            notify = true
            val message = message_chat_activity.text.toString()
            if (message == ""){
                Toast.makeText(this,"Please Enter a message",Toast.LENGTH_SHORT).show()
            }else{
                sendMessage(firebaseUser!!.uid,receiverUserID,message)
            }
        }

        attach_file.setOnClickListener {
            notify = true
            CropImage.activity()
                .start(this)
        }

        seenMessage(receiverUserID)





    }

    private fun sendMessage(sender: String, receiver: String?, message: String) {

        val ref = FirebaseDatabase.getInstance().reference
        val messageKey = ref.push().key
        val map = HashMap<String,Any?>()
        map["sender"] = sender
        map["message"]=message
        map["receiver"] = receiver
        map["isSeen"] = "false"
        map["url"] = ""
        map["messageID"] = messageKey

        ref.child("Messages")
            .child(receiver!!).child(sender!!).child(messageKey!!).setValue(map)

        ref.child("Messages")
            .child(sender).child(receiver!!).child(messageKey!!).setValue(map).addOnCompleteListener {
                if (it.isSuccessful){
                    val listRef = FirebaseDatabase.getInstance().reference.child("MessageList")
                    listRef.child(sender).child(receiver!!).child("id").setValue(receiver)
                    listRef.child(receiver!!).child(sender!!).child("id").setValue(sender).addOnCompleteListener {
                        message_chat_activity!!.text.clear()
                    }
                }
            }

        //notifications
        ref.child("Users").child(sender).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                if (notify){
                    sendNotification(receiver,user!!.getUsername(),message)
                }
                notify = false
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })





    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data!=null){
            val result = CropImage.getActivityResult(data)
            val imageUri = result.uri
            val storageReference = FirebaseStorage.getInstance().reference.child("Message Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            var uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(imageUri!!)
            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot,Task<Uri>>{task->

                if (!task.isSuccessful){

                    task.exception?.let {
                        throw it
                    }

                }

                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val downloadUrl = task.result
                    val myUrl = downloadUrl.toString()

                    val map = HashMap<String,Any?>()
                    map["sender"] = firebaseUser!!.uid
                    map["message"]="sent you an image"
                    map["receiver"] = receiverUserID
                    map["isSeen"] = "false"
                    map["url"] = myUrl
                    map["messageID"] = messageId

                    ref.child("Messages")
                        .child(receiverUserID).child(firebaseUser!!.uid).child(messageId!!).setValue(map)

                    ref.child("Messages")
                        .child(firebaseUser!!.uid).child(receiverUserID).child(messageId!!).setValue(map).addOnCompleteListener {
                            if (it.isSuccessful){


                                //notification
                                ref.child("Users").child(firebaseUser!!.uid).addValueEventListener(object : ValueEventListener{
                                    override fun onDataChange(p0: DataSnapshot) {
                                        val user = p0.getValue(User::class.java)
                                        if (notify){
                                            sendNotification(receiverUserID,user!!.getUsername(),"sent you an image")
                                        }
                                        notify = false
                                    }

                                    override fun onCancelled(p0: DatabaseError) {

                                    }
                                })


                            }
                        }



                }
            }
        }
    }

    private fun userInfo(imageView: ImageView, username: TextView, publisherID:String){
        usersRef = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(publisherID)
        usersRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){

                    val user = p0.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).into(imageView)
                    username.text = user!!.getUsername()

                    retreiveMessages(firebaseUser!!.uid,receiverUserID,user!!.getImage())

                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })
    }

    private fun retreiveMessages(sender: String, receiver: String, receiverImage: String) {

        messageList = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference
            .child("Messages").child(sender).child(receiver)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    messageList!!.clear()
                    for (snapshot in p0.children){
                        val x = snapshot.getValue(message::class.java)
                        messageList!!.add(x!!)
                    }
                    messageAdapter = MessageAdaptor(this@ChatActivity,messageList!!,receiverImage)
                    recyclerView!!.adapter = messageAdapter
                    messageAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }

    private fun sendNotification(receiver: String, username: String, message: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val quey = ref.orderByKey().equalTo(receiver)

        quey.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for(x in p0.children){
                    val token: Token? = x.getValue(Token::class.java)

                    val data = Data(firebaseUser!!.uid,
                        R.drawable.logo,
                        "$username: $message",
                         "New Message",
                           receiverUserID )

                    val sender = Sender(data!!,token!!.getToken().toString())


                    apiService!!.noti(sender).enqueue(object : Callback<MyResponse>{
                        override fun onResponse(
                            call: Call<MyResponse>,
                            response: Response<MyResponse>
                        ) {
                            if (response.code() == 200){
                                if (response.body()!!.success!==1){
                                    Toast.makeText(this@ChatActivity,"Failed",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<MyResponse>, t: Throwable) {

                        }
                    })
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    var seenListener : ValueEventListener? = null
    private fun seenMessage(userID:String){
        val ref = FirebaseDatabase.getInstance().reference.child("Messages")
                .child(userID).child(firebaseUser!!.uid)

        seenListener = ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    for (x in p0.children){
                        val map = HashMap<String,Any?>()
                        map["isSeen"] = "true"
                        x.ref.updateChildren(map)
                    }
                }


            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    private fun online(){
        val ref = FirebaseDatabase.getInstance().reference.child("OnlineUsers")
        ref.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue("true")
    }





}