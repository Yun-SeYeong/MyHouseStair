package com.myhousestair.myhousestair.mock

import com.myhousestair.myhousestair.domain.Member
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithSecurityContextFactory
import org.springframework.stereotype.Component

@Component
class WithMockUserSecurityContextFactory(
    private val passwordEncoder: PasswordEncoder
) : WithSecurityContextFactory<WithMockTestUser> {
    override fun createSecurityContext(annotation: WithMockTestUser?): SecurityContext {
        annotation ?: throw IllegalArgumentException()

        val userDetails = Member(
            email = annotation.email,
            password = passwordEncoder.encode(annotation.password)
        ) as UserDetails

        return SecurityContextHolder.createEmptyContext().apply {
            authentication = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        }
    }
}