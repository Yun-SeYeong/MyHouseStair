package com.myhousestair.myhousestair.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.myhousestair.myhousestair.dto.response.common.ErrorResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils.getToken

@Configuration
class JwtFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : GenericFilterBean() {

    override fun doFilter(
        request: ServletRequest?,
        response: ServletResponse?,
        chain: FilterChain?
    ) {
        val token = (request as HttpServletRequest).getHeader("Authorization")

        if (!token.isNullOrEmpty()) {
            try {
                SecurityContextHolder.getContext().authentication =
                    jwtTokenProvider.getAuthentication(token)
            } catch (e: Exception) {
                response.let {
                    val httpServletResponse = (response as HttpServletResponse)
                    httpServletResponse.contentType = MediaType.APPLICATION_JSON_VALUE
                    httpServletResponse.status = HttpServletResponse.SC_UNAUTHORIZED
                    httpServletResponse.writer?.write(
                        ObjectMapper().writeValueAsString(
                            ErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                e.message ?: ""
                            )
                        )
                    )
                    return
                }
            }
        }

        chain?.doFilter(request, response)
    }

}