package com.app.server.core.security.filter

import com.app.server.common.security.enums.SecurityExceptionCode
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class CustomLogoutFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val httpRequest = request

        // 로그아웃 요청이 들어왔는 지 확인
        if ("/api/auth/sign-out" == httpRequest.requestURI && "POST".equals(
                httpRequest.method, ignoreCase = true
            )
        ) {
            val authentication = SecurityContextHolder.getContext().authentication

            // 인증된 사용자가 아니라면 401 응답을 반환
            if (authentication == null || !authentication.isAuthenticated) {
                request.setAttribute("exception", SecurityExceptionCode.INVALID_AUTHENTICATION)
            }
        }
        filterChain.doFilter(request, response)
    }
}
