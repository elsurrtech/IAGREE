package com.app.iagree.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.app.iagree.R
import com.app.iagree.model.testing

class testingAdaptor(mContext:Context,mTest:MutableList<testing>?)
    :RecyclerView.Adapter<testingAdaptor.ViewHolder?>()
{

    private var mTest : MutableList<testing>? = null
    private var mContext:Context? = null

    init {
        this.mTest = mTest
        this.mContext = mContext
    }

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var textView = itemView.findViewById<TextView>(R.id.text_testing)
    }

    fun addAll(newItem:MutableList<testing>){
        val initSize = mTest!!.size
        mTest!!.addAll(newItem)
        notifyItemRangeChanged(initSize,newItem.size)
    }

    public fun getLastItemID():String{
        return mTest!![mTest!!.size - 1].getname()
    }

    fun removeLastitem(){
        mTest!!.removeAt(mTest!!.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =  LayoutInflater.from(mContext).inflate(R.layout.testing_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mTest!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val test = mTest!![position]
        holder.textView.text = test.getname()
    }
}