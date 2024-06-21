package com.myhousestair.myhousestair.dto.response.auth

data class TokenResponse (
    val accessToken: String,
    val refreshToken: String
)