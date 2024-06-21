package com.myhousestair.myhousestair.dto.response.auth

import com.myhousestair.myhousestair.domain.Member


data class MemberResponse(
    val id: String?,
    val email: String
) {
    companion object {
        fun of(member: Member): MemberResponse {
            return MemberResponse(
                id = member.id.toString(),
                email = member.email
            )
        }
    }
}