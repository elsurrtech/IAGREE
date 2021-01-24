package com.app.iagree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.text.TextUtils
import android.view.View
import android.widget.AbsListView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.adaptor.PostAdaptor
import com.app.iagree.adaptor.testingAdaptor
import com.app.iagree.model.Post
import com.app.iagree.model.testing
import com.google.firebase.database.*
import com.jgabrielfreitas.core.BlurImageView
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_testing.*

class TestingActivity : AppCompatActivity() {

    private var testingAdaptor: testingAdaptor? = null
    private var testing: MutableList<testing>? = null
    private var recyclerView:RecyclerView? = null

    var totalItems = 0
    var scrollOutItems = 0
    var token : String = ""
    var currentItem :Int = 0
    var isLoading = false

    var ysd = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)

        recyclerView = findViewById(R.id.recyclerView_testing_activity)

        progressbar_testing_activity!!.visibility =View.VISIBLE

        val linearLayoutManager:LinearLayoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = linearLayoutManager
        testing = ArrayList()
        testingAdaptor = testingAdaptor(this,testing)
        recyclerView!!.adapter = testingAdaptor

        getData()
        var count = 0;

        loadMore.setOnClickListener {
            val startingRef : Query = FirebaseDatabase.getInstance().reference.child("testing").orderByChild("name").startAt(token).limitToFirst(3)
            startingRef.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()){
                        for (snapshot in p0.children){
                            val x = snapshot.getValue(com.app.iagree.model.testing::class.java)
                            testing!!.add(x!!)
                            token = snapshot.key.toString()
                            count++
                        }
                        if (count >= ysd-4){
                            loadMore!!.visibility = View.GONE
                        }
                        val secRef = FirebaseDatabase.getInstance().reference.child("testing").orderByChild("name").startAt(token).limitToFirst(2)
                        secRef.addListenerForSingleValueEvent(object :ValueEventListener{
                            override fun onDataChange(goti: DataSnapshot) {
                                for (y in goti.children){
                                    token = y.key.toString()
                                }

                            }

                            override fun onCancelled(goti: DatabaseError) {

                            }
                        })
                        testingAdaptor!!.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }

        getTotal()

    }

    fun getTotal(){
        val ref = FirebaseDatabase.getInstance().reference.child("testing")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (x in p0.children){
                    testing_text?.text = x.key.toString()
                }
                ysd=p0.childrenCount.toInt()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun getData(){
        val ref : Query = FirebaseDatabase.getInstance().reference.child("testing").limitToFirst(3)

        if (token==""){
            progressbar_testing_activity?.visibility = View.VISIBLE
            ref.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()){
                        testing!!.clear()
                        for (snapshot in p0.children){

                                    val x = snapshot.getValue(com.app.iagree.model.testing::class.java)
                                    testing!!.add(x!!)
                                    token = snapshot.key.toString()


                        }

                        val secRef = FirebaseDatabase.getInstance().reference.child("testing").orderByChild("name").startAt(token).limitToFirst(2)
                        secRef.addListenerForSingleValueEvent(object :ValueEventListener{
                            override fun onDataChange(goti: DataSnapshot) {
                                for (y in goti.children){
                                    token = y.key.toString()
                                }

                            }

                            override fun onCancelled(goti: DatabaseError) {

                            }
                        })

                        testingAdaptor!!.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })

            progressbar_testing_activity?.visibility = View.GONE
        }
    }

}
