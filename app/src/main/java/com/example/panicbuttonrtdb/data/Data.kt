package com.example.panicbuttonrtdb.data

import com.google.firebase.database.PropertyName

// Data class untuk User
data class User (
    val name: String = "",
    val houseNumber: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val note: String = "",
    val role: String = "user", // <-- TAMBAHAN BARU, default role adalah "user"

    @get:PropertyName("profileImage")
    @set:PropertyName("profileImage")
    var imageProfile: String = "",

    @get:PropertyName("coverImage")
    @set:PropertyName("coverImage")
    var coverImage: String = ""

    // Constructor kosong tidak lagi diperlukan jika semua properti punya nilai default
)

// Data class ini sudah benar, tidak perlu diubah.
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

// Data class ini untuk UI Onboarding, tidak perlu diubah.
data class OnBoardingData(
    val image: Int,
    val title: String,
    val desc: String
)