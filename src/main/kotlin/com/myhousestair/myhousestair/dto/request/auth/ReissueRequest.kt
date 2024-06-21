package com.myhousestair.myhousestair.dto.request.auth

import jakarta.validation.constraints.NotBlank

data class ReissueRequest(
    @field:NotBlank(message = "accessToken is required value")
    val accessToken: String?,
    @field:NotBlank(message = "refreshToken is required value")
    val refreshToken: String?
)
