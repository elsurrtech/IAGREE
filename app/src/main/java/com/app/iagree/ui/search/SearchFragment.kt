package com.app.iagree.ui.search

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.app.iagree.R
import com.app.iagree.adaptor.MyImagesAdaptor
import com.app.iagree.adaptor.SearchUserItemAdaptor
import com.app.iagree.model.Post
import com.app.iagree.model.User
import com.app.iagree.ui.add.AddViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.android.synthetic.main.search_fragment.view.*
import kotlinx.android.synthetic.main.search_fragment.view.topPosts_recyclerView_search_fragment
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment() {

    var postList: List<Post>? = null
    var myImagesAdaptor: MyImagesAdaptor? = null

    private var recyclerView:RecyclerView? = null
    private var userItemAdaptor: SearchUserItemAdaptor? = null
    private var mUser: MutableList<User>? = null

    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

           val root = inflater.inflate(R.layout.search_fragment, container, false)

        //recyclerView search Profiles
        recyclerView = root.findViewById(R.id.recyclerView_search_fragment)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userItemAdaptor = context?.let { SearchUserItemAdaptor(it,mUser as ArrayList<User>,true)}
        recyclerView?.adapter = userItemAdaptor

        //recycler and Grid View Top Posts
        val topPostsRecyclerView : RecyclerView
        topPostsRecyclerView = root.findViewById(R.id.topPosts_recyclerView_search_fragment)
        topPostsRecyclerView.setHasFixedSize(true)
        val linearLayoutManager : LinearLayoutManager = GridLayoutManager(context,3)
        topPostsRecyclerView.layoutManager = linearLayoutManager

        postList = ArrayList()
        myImagesAdaptor = context?.let { MyImagesAdaptor(it,postList as ArrayList<Post>) }
        topPostsRecyclerView.adapter = myImagesAdaptor
        topPostsRecyclerView?.visibility = View.VISIBLE



        topPosts()

        //SearchView Edit text
        root.searchView.addTextChangedListener(object: TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(root.searchView.text.toString() == ""){
                    recyclerView?.visibility = View.GONE
                }else{
                    recyclerView?.visibility = View.VISIBLE

                    retrieveUsers()


                    searchUser(s.toString().toLowerCase())

                }
            }

        })

        return root
    }

    private fun retrieveUsers(){

        val userRef = FirebaseDatabase.getInstance().getReference().child("Users")
        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                if(view?.searchView?.text.toString() == ""){

                    mUser?.clear()

                    for(snapshot in p0.children){
                        val user = snapshot.getValue(User::class.java)
                        if(user!=null){
                            mUser?.add(user)
                        }
                    }

                    userItemAdaptor?.notifyDataSetChanged()

                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun searchUser(input:String){
        val query= FirebaseDatabase.getInstance().getReference().child("Users")
            .orderByChild("username").startAt(input).endAt(input+"\uf8ff")

        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                mUser?.clear()

                for(snapshot in p0.children){
                    val user = snapshot.getValue(User::class.java)
                    if(user!=null){
                        mUser?.add(user)
                    }
                }

                userItemAdaptor?.notifyDataSetChanged()

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun searchByFullName(input:String){
        val query= FirebaseDatabase.getInstance().getReference().child("Users")
            .orderByChild("fullname").startAt(input).endAt(input+"\uf8ff")

        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                mUser?.clear()

                for(snapshot in p0.children){
                    val user = snapshot.getValue(User::class.java)
                    if(user!=null){
                        mUser?.add(user)
                    }
                }

                userItemAdaptor?.notifyDataSetChanged()

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun topPosts(){
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    (postList as ArrayList<Post>).clear()

                    for (snapshot in p0.children){
                        val post = snapshot.getValue(Post::class.java)!!

                        val privacyRef = FirebaseDatabase.getInstance().reference
                            .child("PrivateAccounts").child(post.getPublisher())
                        privacyRef.addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(goti: DataSnapshot) {
                                if (goti.exists()){
                                    //do nothing
                                }else{
                                    if (!post.getPublisher().equals(firebaseUser!!.uid)){
                                        (postList as ArrayList<Post>).add(post)
                                    }
                                }
                            }

                            override fun onCancelled(p0: DatabaseError) {

                            }
                        })

                        Collections.reverse(postList)
                        myImagesAdaptor!!.notifyDataSetChanged()

                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


}
