package com.app.solutionchallenge.common.security.provider

import com.app.solutionchallenge.common.exception.BadRequestException
import com.app.solutionchallenge.common.security.JwtAuthenticationToken
import com.app.solutionchallenge.common.security.enums.SecurityExceptionCode
import com.app.solutionchallenge.common.security.info.CustomUserDetails
import com.app.solutionchallenge.common.security.info.JwtUserInfo
import com.app.solutionchallenge.common.security.service.CustomUserDetailsService
import lombok.RequiredArgsConstructor
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationProvider(
    private val customUserDetailsService: CustomUserDetailsService
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        if (authentication is JwtAuthenticationToken) {
            val jwtUserInfo: JwtUserInfo = JwtUserInfo(id = authentication.getPrincipal() as Long)

            val userDetails = customUserDetailsService.loadUserByUserId(jwtUserInfo.id) as CustomUserDetails

            return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        }

        throw BadRequestException(SecurityExceptionCode.INVALID_AUTHENTICATION)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == JwtAuthenticationToken::class.java
    }
}