package com.myhousestair.myhousestair.dto.request.auth

import com.myhousestair.myhousestair.domain.Member
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class SignUpRequest(
    @field:NotBlank(message = "email is required value")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}\$",
        message = "email is invalid"
    )
    val email: String?,
    @field:NotBlank(message = "password is required value")
    val password: String?
) {
    fun toMember() = Member(
        email = email!!,
        password = password!!
    )
}
