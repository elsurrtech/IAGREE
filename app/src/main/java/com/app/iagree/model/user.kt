package com.app.iagree.model

class User {
    private var username: String = ""
    private var fullname: String = ""
    private var uid: String = ""
    private var bio: String = ""
    private var image: String = ""
    private var college:String = ""
    private var degree:String = ""
    private var programme:String = ""
    private var phone = ""

    constructor()

    constructor(username:String,fullname:String,uid:String,bio:String,image:String, college:String, degree:String, programme:String, phone:String){

        this.username = username
        this.fullname = fullname
        this.uid = uid
        this.bio = bio
        this.image = image
        this.degree = degree
        this.programme = programme
        this.college = college
        this.phone = phone

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

    fun getCollege():String{
        return college
    }
    fun getProgramme():String{
        return programme
    }

    fun getDegree():String{
        return degree
    }

    fun getPhone():String{
        return phone
    }


}