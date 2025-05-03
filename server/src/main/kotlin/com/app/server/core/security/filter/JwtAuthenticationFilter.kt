package com.app.server.core.security.filter

import com.app.server.common.constant.Constants
import com.app.server.common.security.JwtAuthenticationToken
import com.app.server.common.security.info.JwtUserInfo
import com.app.server.common.security.provider.JwtAuthenticationProvider
import com.app.server.common.security.util.JwtUtil
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.AntPathMatcher
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val jwtAuthenticationProvider: JwtAuthenticationProvider
): OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val requestURI = request.requestURI
        val antPathMatcher = AntPathMatcher()
        return Constants.NO_NEED_AUTH_URLS.stream().anyMatch { pattern: String ->
            antPathMatcher.match(
                pattern, requestURI
            )
        }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = jwtUtil.getJwtFromRequest(request)

            if (StringUtils.hasText(token)) {
                val claims = jwtUtil.validateAndGetClaimsFromToken(token)
                val jwtUserInfo = JwtUserInfo(id = claims.get(Constants.USER_ID_CLAIM_NAME, Long::class.java))

                val beforeAuthentication =
                    JwtAuthenticationToken(null, jwtUserInfo.id)

                val afterAuthentication =
                    jwtAuthenticationProvider
                        .authenticate(beforeAuthentication) as UsernamePasswordAuthenticationToken
                afterAuthentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                val securityContext = SecurityContextHolder.createEmptyContext()
                securityContext.authentication = afterAuthentication
                SecurityContextHolder.setContext(securityContext)
            }

            filterChain.doFilter(request, response)
        } catch (e: JwtException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            request.setAttribute("exception", e)
            filterChain.doFilter(request, response)
        }
    }
}