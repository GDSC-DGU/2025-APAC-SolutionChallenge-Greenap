package com.app.server.core.security.util

import com.app.server.common.constant.Constants
import com.app.server.user.ui.dto.response.JwtTokenDto
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.security.Key
import java.util.*

@Component
class JwtUtil(
    @Value("\${jwt.secret}")
    private val secretKey: String,

    @Value("\${jwt.access-token-validity-in-milli-seconds}")
    private val accessTokenExpirationPeriod: Long,

    @Value("\${jwt.refresh-token-validity-in-milli-seconds}")
    private val refreshTokenExpirationPeriod: Long,
) {

    private val key: Key by lazy {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        Keys.hmacShaKeyFor(keyBytes)
    }

    fun createToken(id: Long, email: String, role: String, expirationPeriod: Long?): String {
        val claims = Jwts.claims()
        claims[Constants.USER_ID_CLAIM_NAME] = id
        claims[Constants.USER_EMAIL_CLAIM_NAME] = email
        claims[Constants.USER_ROLE_CLAIM_NAME] = role

        val now = Date()
        val tokenValidity = Date(now.time + expirationPeriod!!) // 토큰의 만료시간 설정

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(tokenValidity)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    fun generateToken(id: Long, email: String, role: String): JwtTokenDto {
        return JwtTokenDto(
            createToken(id, email, role, accessTokenExpirationPeriod),
            createToken(id, email, role, refreshTokenExpirationPeriod)
        )
    }

    fun validateToken(token: String?): Boolean {
        val jwtParser = Jwts.parserBuilder().setSigningKey(key).build()
        return try {
            jwtParser.parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun validateAndGetClaimsFromToken(token: String?): Claims {
        val jwtParser = Jwts.parserBuilder().setSigningKey(key).build()
        return jwtParser.parseClaimsJws(token).body
    }

    fun getJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(Constants.AUTHORIZATION_HEADER)
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(Constants.BEARER_PREFIX)) {
            return bearerToken.substring(Constants.BEARER_PREFIX.length)
        }
        return null
    }

    fun getUserIdFromToken(token: String?): Long {
        val claims = validateAndGetClaimsFromToken(token)
        return claims.get(Constants.USER_ID_CLAIM_NAME, Long::class.java)
    }
}