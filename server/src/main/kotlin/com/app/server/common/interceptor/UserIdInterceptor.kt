package com.app.server.common.interceptor

import com.app.server.common.enums.CommonResultCode
import com.app.server.common.exception.UnauthorizedException
import com.app.server.common.security.info.CustomUserDetails
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.extern.slf4j.Slf4j
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class UserIdInterceptor : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw UnauthorizedException(CommonResultCode.AUTH_EXCEPTION)
        val userDetails = authentication.principal as CustomUserDetails
        request.setAttribute("USER_ID", userDetails.getId())
        return super.preHandle(request, response, handler)
    }
}
