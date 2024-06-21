package com.myhousestair.myhousestair.config

import com.myhousestair.myhousestair.dto.response.auth.TokenResponse
import com.myhousestair.myhousestair.exception.BadRequestException
import com.myhousestair.myhousestair.exception.UnAuthorizationException
import com.myhousestair.myhousestair.repository.MemberRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val memberRepository: MemberRepository
) {
    private val shaKey = "ysy_blog_api_server_jwt_token_secret_key"
    private val accessTTL = (24 * 60 * 60 * 1000).toLong()
    private val refreshTTL = (20 * 24 * 60 * 60 * 1000).toLong()

    fun generateToken(authentication: Authentication): TokenResponse {
        val authorities = authentication.authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .toList()

        val now = Date()
        val accessExpiredDate = Date(now.time + accessTTL)
        val refreshExpiredDate = Date(now.time + refreshTTL)

        val secretKey: SecretKey = Keys.hmacShaKeyFor(shaKey.toByteArray(StandardCharsets.UTF_8))

        val accessToken = Jwts.builder()
            .setSubject(authentication.name)
            .claim("auth", authorities)
            .setExpiration(accessExpiredDate)
            .signWith(secretKey)
            .compact()

        val refreshToken = Jwts.builder()
            .setSubject(authentication.name)
            .setExpiration(refreshExpiredDate)
            .signWith(secretKey)
            .compact()

        return TokenResponse(
            accessToken = "Bearer $accessToken",
            refreshToken = "Bearer $refreshToken"
        )
    }

    fun reissueToken(accessToken: String, refreshToken: String): TokenResponse {
        val accessTokenClaims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(shaKey.toByteArray(StandardCharsets.UTF_8)))
            .build()
            .parseClaimsJws(getToken(accessToken))
            .body

        val refreshTokenClaims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(shaKey.toByteArray(StandardCharsets.UTF_8)))
            .build()
            .parseClaimsJws(getToken(refreshToken))
            .body

        if (refreshTokenClaims.expiration.before(Date())) {
            throw UnAuthorizationException("Token is expired")
        }

        if (!accessTokenClaims.subject.equals(refreshTokenClaims.subject)) {
            throw UnAuthorizationException("Invalid Token")
        }

        val authorities = accessTokenClaims["auth"].toString().split(",")
            .map(::SimpleGrantedAuthority)
            .toList()

        val userDetails = memberRepository.findByEmail(accessTokenClaims.subject).orElseThrow()

        val authentication =
            UsernamePasswordAuthenticationToken(userDetails, "", authorities)

        return generateToken(authentication)
    }

    fun getAuthentication(accessToken: String): Authentication {
        val claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(shaKey.toByteArray(StandardCharsets.UTF_8)))
            .build()
            .parseClaimsJws(getToken(accessToken))
            .body

        val authorities = claims["auth"].toString().split(",")
            .map(::SimpleGrantedAuthority)
            .toList()

        val userDetails = memberRepository.findByEmail(claims.subject).orElseThrow()

        return UsernamePasswordAuthenticationToken(userDetails, "", authorities)
    }

    fun getToken(authorization: String): String? {
        var token: String? = null
        if (authorization.startsWith("Bearer ")) {
            token = authorization.substring(7)
        }
        return token
    }
}