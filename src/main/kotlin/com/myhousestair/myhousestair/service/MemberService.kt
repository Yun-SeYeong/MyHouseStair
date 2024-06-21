package com.myhousestair.myhousestair.service

import com.myhousestair.myhousestair.config.JwtTokenProvider
import com.myhousestair.myhousestair.dto.request.auth.ReissueRequest
import com.myhousestair.myhousestair.dto.request.auth.SignInRequest
import com.myhousestair.myhousestair.dto.request.auth.SignUpRequest
import com.myhousestair.myhousestair.dto.response.auth.MemberResponse
import com.myhousestair.myhousestair.dto.response.auth.TokenResponse
import com.myhousestair.myhousestair.dto.response.common.CommonResponse
import com.myhousestair.myhousestair.repository.MemberRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val tokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    fun reissue(reissueRequest: ReissueRequest): CommonResponse<TokenResponse> {
        return CommonResponse(
            tokenProvider.reissueToken(
                reissueRequest.accessToken!!,
                reissueRequest.refreshToken!!
            )
        )
    }

    fun signIn(signInRequest: SignInRequest): CommonResponse<TokenResponse> {
        val token = UsernamePasswordAuthenticationToken(signInRequest.email, signInRequest.password)

        authenticationManagerBuilder.`object`.authenticate(token)

        return CommonResponse(tokenProvider.generateToken(token))
    }

    fun createMember(signUpRequest: SignUpRequest): CommonResponse<MemberResponse> {
        return CommonResponse(
            MemberResponse.of(
                memberRepository.save(
                    signUpRequest.copy(
                        password = passwordEncoder.encode(signUpRequest.password)
                    ).toMember()
                )
            )
        )
    }

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String?) =
        memberRepository.findByEmail(username!!)
            .orElseThrow() as UserDetails
}