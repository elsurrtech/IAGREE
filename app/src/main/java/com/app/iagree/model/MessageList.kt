package com.app.iagree.model

import android.content.Context

class MessageList {
    private var id :String = ""
    constructor()

    constructor(id:String){
        this.id = id
    }

    fun getId():String{
        return id
    }
}