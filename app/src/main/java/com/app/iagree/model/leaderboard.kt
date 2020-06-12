package com.app.iagree.model

class leaderboard {
    private var uid: String = ""

    constructor()
    constructor(uid: String) {
        this.uid = uid
    }

    //getters

    fun getuid(): String{
        return uid
    }


    //setters
    fun setuid(uid: String){
        this.uid = uid
    }

}