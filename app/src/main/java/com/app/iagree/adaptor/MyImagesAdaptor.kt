package com.app.iagree.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.agrawalsuneet.dotsloader.loaders.LazyLoader
import com.app.iagree.PostDetailsFragment
import com.app.iagree.R
import com.app.iagree.SearchProfileFragment
import com.app.iagree.model.Post
import com.squareup.picasso.Picasso
import java.lang.Exception

class MyImagesAdaptor (private val mContext:Context, mPost:List<Post>)
    : RecyclerView.Adapter<MyImagesAdaptor.ViewHolder?>() {

    private var mPost: List<Post>? = null


    init {
        this.mPost = mPost
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyImagesAdaptor.ViewHolder {
        val view =  LayoutInflater.from(mContext).inflate(R.layout.dashboard_myimages_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost!!.size
    }

    override fun onBindViewHolder(holder: MyImagesAdaptor.ViewHolder, position: Int) {

        val post: Post = mPost!![position]
        Picasso.get().load(post.getPostImage()).resize(300,300).onlyScaleDown().into(holder.postImage, object :com.squareup.picasso.Callback{
            override fun onSuccess() {
                holder.loaderRelative.visibility = View.GONE
            }

            override fun onError(e: Exception?) {

            }
        })

        holder.postImage.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            editor.putString("postID",post.getPostID())
            editor.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .add(R.id.nav_host_fragment,PostDetailsFragment()).commit()
        }

        val loader = LazyLoader(mContext,5,20, ContextCompat.getColor(mContext, R.color.white),
            ContextCompat.getColor(mContext, R.color.white),
            ContextCompat.getColor(mContext, R.color.white)).apply {
            animDuration = 1500
            firstDelayDuration = 100
            secondDelayDuration = 200
            interpolator = DecelerateInterpolator()
        }
        holder.loaderLinear.addView(loader)


    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var postImage: ImageView = itemView.findViewById(R.id.post_image)
        val loaderRelative : RelativeLayout = itemView.findViewById(R.id.loader_layout_myImages)
        val loaderLinear : LinearLayout = itemView.findViewById(R.id.loader_myImages)
    }
}