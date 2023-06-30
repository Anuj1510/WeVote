package com.example.wevote.data

class Voter {
    var uid: String? = null
    var VoterImage: Map<String, String>? = null
    var Party:String? = null

    constructor(){}

    constructor(
        uid:String?,
        VoterImage: Map<String, String>,
        Party:String?
    ){
        this.VoterImage = VoterImage
        this.uid = uid
        this.Party = Party
    }

}