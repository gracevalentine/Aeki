package com.example.myaeki.Authentication.Model

import UserProfile

data class UserProfileResponse(
    val message: String,
    val user: UserProfile
)