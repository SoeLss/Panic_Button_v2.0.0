package com.example.panicbuttonrtdb.data

import com.google.firebase.database.PropertyName

// Data class untuk User
data class User (
    val name: String,
    val houseNumber: String,
    val password: String,
    val phoneNumber: String = "",
    val note: String = "",

    @get:PropertyName("profileImage") //getter
    @set:PropertyName("profileImage") //setter
    var imageProfile: String = "",

    @get:PropertyName("coverImage")
    @set:PropertyName("coverImage")
    var coverImage: String = ""

) {
    // Constructor tanpa argumen diperlukan oleh Firebase
    constructor() : this("", "", "")
}

data class MonitorRecord(
    val id: String = "",
    val name: String = "",
    val houseNumber: String = "",
    val message: String = "",
    val priority: String = "",
    val time: String = "",
    val status: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class OnBoardingData(
    val image: Int,
    val title: String,
    val desc: String
)
