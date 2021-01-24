package com.app.iagree

import android.content.Context
import android.content.SharedPreferences

class SaveLanguage(context: Context) {
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("Language", Context.MODE_PRIVATE)

    fun setLanguage(language: String?){
        val editor = sharedPreferences.edit()
        editor.putString("language",language!!)
        editor.apply()
    }

    fun setCollege(college:String?){
        val editor = sharedPreferences.edit()
        editor.putString("college",college!!)
        editor.apply()
    }

    //This fun will load night mode state
    fun loadLanguage(): String? {
        val state = sharedPreferences.getString("language","")
        return (state)
    }

    fun loadCollege():String?{
        val state = sharedPreferences.getString("college","")
        return (state)
    }

}