package com.app.iagree.model

class notification {

    private var userID: String = ""
    private var text: String = ""
    private var postID: String = ""
    private var isPost = false

    constructor()

    constructor(userID: String, text: String, postID: String, isPost: Boolean) {
        this.userID = userID
        this.text = text
        this.postID = postID
        this.isPost = isPost
    }

    fun getuserID():String{
        return userID
    }

    fun gettext():String{
        return text
    }

    fun getpostID():String{
        return postID
    }

    fun isPost():Boolean{
        return isPost
    }

    //setters
    fun setuserID(userID: String){
        this.userID = userID
    }

    fun settext(text: String){
        this.text = text
    }

    fun setpostID(postID: String){
        this.postID = postID
    }

    fun setisPost(isPost: Boolean){
        this.isPost = isPost
    }

}