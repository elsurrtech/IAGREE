package com.app.iagree.questionare.adaptor

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.R
import com.app.iagree.model.User
import com.app.iagree.questionare.model.question
import com.borjabravo.readmoretextview.ReadMoreTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import xyz.hanks.library.bang.SmallBangView

class questionAdaptor(private var mContext:Context,private var mQuestion:MutableList<question>):
RecyclerView.Adapter<questionAdaptor.ViewHolder>()
{


    inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView){
        var userImage = itemView.findViewById<CircleImageView>(R.id.profileImage_q_post)
        var Name = itemView.findViewById<TextView>(R.id.username_q_post)
        var bio = itemView.findViewById<ReadMoreTextView>(R.id.bio_q_post)
        var question = itemView.findViewById<TextView>(R.id.question_q_post)
        var details = itemView.findViewById<ReadMoreTextView>(R.id.details_q_post)
        var Date  = itemView.findViewById<TextView>(R.id.date_q_post)
        var likePost = itemView.findViewById<SmallBangView>(R.id.like_heart_q_post)
        var likes = itemView.findViewById<TextView>(R.id.likes_q_post)
        var commentIcon = itemView.findViewById<ImageView>(R.id.comment_icon_q_post)
        var comments = itemView.findViewById<TextView>(R.id.comments_q_post)
        var saveQuestion = itemView.findViewById<SmallBangView>(R.id.save_smallBang_q_post)
        var answers = itemView.findViewById<LinearLayout>(R.id.answers_question_post)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =  LayoutInflater.from(mContext).inflate(R.layout.question_post_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mQuestion!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = mQuestion[position]
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        holder.bio.visibility = View.GONE
        holder.Name.text = question.getname()
        if (question.getname() != "Anonymous"){
            holder.bio.visibility=View.VISIBLE
            getUserInfo(holder.bio,holder.userImage,question.getuid())
        }else{
            holder.bio.visibility=View.INVISIBLE
        }
        holder.question.text = question.getquestion()
        holder.details.text = question.getdescription()
        holder.Date.text = question.getdate()

        isLiked(question.getuid(),holder.likePost)
        isSaved(question.getuid(),holder.saveQuestion)

        holder.answers.setOnClickListener {

        }

        holder.likePost.setOnClickListener{
            if (holder.likePost.isSelected){
                holder.likePost.isSelected = false
                FirebaseDatabase.getInstance().reference.child("QuestionLikes").child(question.getkey())
                    .child(firebaseUser!!.uid).removeValue()
            }else{
                holder.likePost.isSelected = true
                holder.likePost.likeAnimation(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {

                    }
                })

                FirebaseDatabase.getInstance().reference.child("QuestionLikes").child(question.getkey())
                    .child(firebaseUser!!.uid).setValue(true)
            }
        }



        holder.saveQuestion.setOnClickListener {
            if (holder.saveQuestion.isSelected){
                holder.saveQuestion.isSelected = false
                FirebaseDatabase.getInstance().reference.child("QuestionSaves").child(question.getkey())
                    .child(firebaseUser!!.uid).removeValue()
            }else{
                holder.saveQuestion.isSelected = true
                holder.saveQuestion.likeAnimation(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {

                    }
                })

                FirebaseDatabase.getInstance().reference.child("QuestionSaves").child(question.getkey())
                    .child(firebaseUser!!.uid).setValue(true)
            }
        }

        getLikes(holder.likes,question.getkey())
        getComments(holder.comments,question.getkey())

    }

    private fun getLikes(t:TextView,key:String){
        val ref = FirebaseDatabase.getInstance().reference.child("QuestionLikes").child(key)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    t.text = p0.childrenCount.toString() + "Likes"
                }else{
                    t.text = "0 Likes"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun getComments(t:TextView,key:String){
        val ref = FirebaseDatabase.getInstance().reference.child("QuestionComments").child(key)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    t.text = p0.childrenCount.toString() + "Answers"
                }else{
                    t.text = "0 Answers"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun isLiked(id:String,like:SmallBangView){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance()
            .reference.child("QuestionLikes").child(id)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(firebaseUser!!.uid).exists()){
                    like.isSelected = true
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun isSaved(id:String,save:SmallBangView){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance()
            .reference.child("QuestionSaves").child(id)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(firebaseUser!!.uid).exists()){
                    save.isSelected = true
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun getUserInfo(bio: TextView, imageProfile: ImageView, publisher: String) {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisher)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val user = p0.getValue(User::class.java)
                    Picasso.get().load(user!!.getImage()).resize(200,200).onlyScaleDown().placeholder(R.drawable.ic_account_circle_black_24dp).into(imageProfile)
                    bio.text = user!!.getBio()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }
}