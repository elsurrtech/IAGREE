package com.app.iagree.questionare.model

class question {
    private var description = ""
    private var key = ""
    private var name = ""
    private var question = ""
    private var uid = ""
    private var username = ""
    private var date = ""
    private var type = ""

    constructor()
    constructor(description:String,key:String,name:String,question:String,uid:String,username:String,date:String, type:String){
        this.description = description
        this.key = key
        this.name = name
        this.question = question
        this.uid = uid
        this.username= username
        this.date = date
        this.type = type
    }

    fun getdescription():String{
        return description
    }
    fun getkey():String{
        return key
    }
    fun getname():String{
        return name
    }
    fun getquestion():String{
        return question
    }
    fun getuid():String{
        return uid
    }
    fun getusername():String{
        return username
    }
    fun getdate():String{
        return date
    }

    fun gettype():String{
        return type
    }

}