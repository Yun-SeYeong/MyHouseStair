package com.myhousestair.myhousestair.controller


import com.myhousestair.myhousestair.dto.request.auth.ReissueRequest
import com.myhousestair.myhousestair.dto.request.auth.SignInRequest
import com.myhousestair.myhousestair.dto.request.auth.SignUpRequest
import com.myhousestair.myhousestair.service.MemberService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val memberService: MemberService
) {

    @PostMapping("/signin")
    fun signIn(
        @Validated @RequestBody signInRequest: SignInRequest
    ) = memberService.signIn(signInRequest)

    @PostMapping("/signup")
    fun signUp(
        @Validated @RequestBody signUpRequest: SignUpRequest
    ) = memberService.createMember(signUpRequest)

    @PostMapping("/reissue")
    fun reissue(
        @Validated @RequestBody reissueRequest: ReissueRequest
    ) = memberService.reissue(reissueRequest)
}