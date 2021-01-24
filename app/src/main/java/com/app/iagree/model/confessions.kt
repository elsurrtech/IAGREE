package com.app.iagree.model

class confessions {
    private var key: String = ""
    private var message: String = ""
    private var publisher : String = ""




    constructor()
    constructor(key: String,message:String, publisher:String) {
        this.key= key
        this.message = message
        this.publisher = publisher
    }

    //getters
    fun getKey():String{
        return key
    }

    fun getMessage():String{
        return message
    }

    fun getPublisher():String{
        return publisher
    }



}