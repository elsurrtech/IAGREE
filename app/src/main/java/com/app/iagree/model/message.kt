package com.app.iagree.model

class message {

    private var isSeen : String = ""
    private var message : String = ""
    private var messageID : String = ""
    private var receiver : String = ""
    private var sender : String = ""
    private var url : String = ""

    constructor()
    constructor(isSeen:String,message: String, messageID:String,receiver : String, sender:String, url :String){
        this.isSeen = isSeen
        this.message = message
        this.messageID = messageID
        this.receiver  = receiver
        this.sender = sender
        this.url = url
    }

    //getters
    fun getisSeen():String{
        return  isSeen
    }

    fun getmessage():String{
        return  message
    }

    fun getmessageID():String{
        return  messageID
    }

    fun getreceiver():String{
        return  receiver
    }

    fun getsender():String{
        return  sender
    }

    fun geturl():String{
        return  url
    }

    //setters
    fun setisSeen(isSeen: String){
        this.isSeen=isSeen
    }

    fun setmessage(message: String){
        this.message=message
    }

    fun setmessageID(messageID: String){
        this.messageID=messageID
    }

    fun setreceiver(receiver: String){
        this.receiver=receiver
    }

    fun setsender(sender: String){
        this.sender=sender
    }

    fun seturl(url: String){
        this.url=url
    }

}