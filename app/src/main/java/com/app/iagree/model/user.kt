package com.app.iagree.model

class User {
    private var username: String = ""
    private var fullname: String = ""
    private var uid: String = ""
    private var bio: String = ""
    private var image: String = ""

    constructor()

    constructor(username:String,fullname:String,uid:String,bio:String,image:String){

        this.username = username
        this.fullname = fullname
        this.uid = uid
        this.bio = bio
        this.image = image

    }

    fun getUsername():String{
        return username
    }

    fun setUsername(username: String){
        this.username = username
    }

    fun getFullname():String{
        return fullname
    }

    fun setFullname(fullname: String){
        this.fullname = fullname
    }

    fun getUID():String{
        return uid
    }

    fun setUID(uid: String){
        this.uid = uid
    }

    fun getBio():String{
        return bio
    }

    fun setBio(bio: String){
        this.bio = bio
    }

    fun getImage():String{
        return image
    }

    fun setImage(image: String){
        this.image = image
    }


}