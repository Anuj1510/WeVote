package com.example.wevote.data

class Voter {
    var uid: String? = null
    var Name:String? = null
    var VoterImage: String? = null
    var VoterEmail:String? = null
    var AadharImage:String? = null
    var Party:String? = null
    var ProfileImage:String? = null

    constructor(){}

    constructor(
        uid:String?,
        Name:String?,
        VoterImage: String?,
        VoterEmail:String?,
        AadharImage:String?,
        Party:String?,
        ProfileImage:String?
    ){
        this.VoterImage = VoterImage
        this.Name = Name
        this.AadharImage = AadharImage
        this.VoterEmail = VoterEmail
        this.uid = uid
        this.Party = Party
        this.ProfileImage = ProfileImage
    }

}