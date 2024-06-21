package com.myhousestair.myhousestair.repository

import com.myhousestair.myhousestair.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface MemberRepository : JpaRepository<Member, UUID> {
    fun findByEmail(email: String): Optional<Member>
}