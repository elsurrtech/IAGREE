package com.app.iagree.model

class Post {

    private var postID: String = ""
    private var postImage: String = ""
    private var publisher: String = ""
    private var description: String = ""

    constructor()
    //contructor
    constructor(postID: String, postImage: String, publisher: String, description: String) {
        this.postID = postID
        this.postImage = postImage
        this.publisher = publisher
        this.description = description
    }

    //getters
    fun getPostID():String{
        return postID
    }

    fun getPostImage():String{
        return postImage
    }

    fun getPublisher():String{
        return publisher
    }

    fun getDescription():String{
        return description
    }

    //setters
    fun setPostID(postID: String){
        this.postID = postID
    }

    fun setPostImage(postImage: String){
        this.postImage = postImage
    }

    fun setPublisher(publisher: String){
        this.publisher = publisher
    }

    fun setDescription(description: String){
        this.description = description
    }


}