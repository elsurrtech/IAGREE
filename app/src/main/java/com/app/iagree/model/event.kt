package com.app.iagree.model

import kotlin.math.max

class event {
    private var address     :String = ""
    private var date        :String = ""
    private var description :String = ""
    private var eventDate   :String = ""
    private var eventID     :String = ""
    private var fees        :String = ""
    private var maxPeople   :String = ""
    private var phone       :String = ""
    private var postImage   :String = ""
    private var publisher   :String = ""
    private var title       :String = ""
    private var eventTime   :String = ""
    private var link        :String = ""

    constructor()

    constructor(address:String, date:String,description:String, eventDate:String, eventID:String, fees:String, maxPeople:String, phone:String, postImage:String, publisher:String,link:String, title:String, eventTime:String)
    {
        this.address = address
        this.date = date
        this.eventDate = eventDate
       this.description = description
       this.eventID = eventID
       this.fees = fees
       this.maxPeople = maxPeople
       this.phone = phone
       this.postImage = postImage
        this.publisher = publisher
        this.title = title
        this.eventTime = eventTime
        this.link = link

    }

    fun getAddress():String{
        return address
    }

    fun getDate():String{
        return date
    }

    fun geteventDate():String{
        return eventDate
    }

    fun getDescription():String{
        return description
    }

    fun geteventID():String{
        return eventID
    }

    fun getFees():String{
        return fees
    }

    fun getmaxPeople():String{
        return maxPeople
    }

    fun getPhone():String{
        return phone
    }

    fun getpostImage():String{
        return postImage
    }

    fun getPublisher():String{
        return publisher
    }

    fun getTitle():String{
        return title
    }

    fun geteventTime():String{
        return eventTime
    }

    fun getLink():String{
        return link
    }

}