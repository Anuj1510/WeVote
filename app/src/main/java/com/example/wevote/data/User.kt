package com.example.wevote.data

class User {

    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    var PhoneNumber:Long? = null
    var UserImage: Map<String, String>? = null
    var uid: String? = null

    constructor(){}

    constructor(
        firstName:String?,
        lastName:String?,
        email:String?,
        PhoneNumber: Long,
        uid:String?,
        UserImage: Map<String, String>
    ){
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
        this.PhoneNumber = PhoneNumber
        this.UserImage = UserImage
        this.uid = uid
    }

}
