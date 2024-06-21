package com.myhousestair.myhousestair.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.myhousestair.myhousestair.domain.Member
import com.myhousestair.myhousestair.dto.request.auth.SignInRequest
import com.myhousestair.myhousestair.dto.response.common.CommonResponse
import com.myhousestair.myhousestair.dto.response.auth.MemberResponse
import com.myhousestair.myhousestair.repository.MemberRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.NoSuchElementException

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @MockkBean
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun signIn() {
        val signInRequest = SignInRequest(
            email = "test@test.com",
            password = "test"
        )

        every {
            memberRepository.findByEmail(signInRequest.email!!).orElseThrow()
        } returnsMany listOf(
            Member(
                "test@test.com",
                passwordEncoder.encode(signInRequest.password)
            )
        )

        mockMvc.perform(
            post("/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(signInRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.accessToken").isString)
            .andExpect(jsonPath("$.content.refreshToken").isString)
    }

    @Test
    fun signInFail() {
        val signInRequest = SignInRequest(
            email = "test@test.com",
            password = "test"
        )

        every {
            memberRepository.findByEmail(signInRequest.email!!).orElseThrow()
        } throws NoSuchElementException("No value present")

        mockMvc.perform(
            post("/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(signInRequest))
        ).andExpect(status().isForbidden)

        verify(exactly = 1) { memberRepository.findByEmail(signInRequest.email!!).orElseThrow() }
    }

    @Test
    fun signUp() {
        val signUpRequest = SignInRequest(
            email = "test@test.com",
            password = "test"
        )
        val saveMember = signUpRequest.toMember()
        val memberResponse = MemberResponse.of(saveMember)

        every {
            memberRepository.save(saveMember)
        } returns saveMember

        mockMvc.perform(
            post("/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(signUpRequest))
        )
            .andExpect(status().isOk)
            .andExpect(
                content().string(objectMapper.writeValueAsString(
                CommonResponse(memberResponse)
            )))

        verify(exactly = 1) { memberRepository.save(saveMember) }
    }
}