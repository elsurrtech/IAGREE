package com.app.iagree.model

class Story {

    private var imageurl: String = ""
    private var timestart: Long = 0
    private var timeend: Long = 0
    private var storyid: String = ""
    private var userid: String = ""
    private var desc: String = ""

    constructor()
    constructor(imageurl: String, timestart: Long, timeend: Long, storyid: String, userid: String,desc:String) {
        this.imageurl = imageurl
        this.timestart = timestart
        this.timeend = timeend
        this.storyid = storyid
        this.userid = userid
        this.desc = desc
    }


    //getters
    fun getImageUrl(): String{
        return imageurl
    }

    fun getTimeStart(): Long{
        return timestart
    }

    fun getTimeEnd(): Long{
        return timeend
    }

    fun getStoryID(): String{
        return storyid
    }

    fun getUserID(): String{
        return userid
    }

    fun getDesc():String{
        return desc
    }

    //setters
    fun setImageUrl(imageurl: String){
        this.imageurl = imageurl
    }

    fun setTimeStart(timestart: Long){
        this.timestart = timestart
    }

    fun setTimeEnd(timeend: Long){
        this.timeend = timeend
    }

    fun setStoryID(storyid: String){
        this.storyid = storyid
    }

    fun setUserID(userid: String){
        this.userid = userid
    }

    fun setDesc(desc: String){
        this.desc = desc
    }

}