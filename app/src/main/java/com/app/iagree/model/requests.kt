package com.app.iagree.model

class requests {
    private var requestPublisher: String = ""

    constructor()
    constructor(requestPublisher: String) {
        this.requestPublisher = requestPublisher
    }

    fun getrequestPublisher():String{
        return requestPublisher
    }

    fun setComment(requestPublisher: String){
        this.requestPublisher = requestPublisher
    }
}