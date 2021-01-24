package com.app.iagree.Notifications

class Data {

    private var user: String = ""
    private var icon = 0
    private var body: String = ""
    private var title: String = ""
    private var sent: String = ""

    constructor()
    constructor(user: String, icon: Int, body: String, title: String, sent: String) {
        this.user = user
        this.icon = icon
        this.body = body
        this.title = title
        this.sent = sent
    }

    //getters
    fun getUser():String{
        return user
    }

    fun getIcon():Int{
        return icon
    }

    fun getBody():String{
        return body
    }

    fun getTitle():String{
        return title
    }

    fun getSent():String{
        return sent
    }

    //setters
    fun setUser(user: String){
        this.user = user
    }

    fun setIcon(icon: Int){
        this.icon = icon
    }

    fun setBody(body: String){
        this.body = body
    }

    fun setTitle(title: String){
        this.title = title
    }

    fun setSent(sent: String){
        this.sent = sent
    }

}