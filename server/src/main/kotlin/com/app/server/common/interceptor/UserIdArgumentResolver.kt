package com.app.server.common.interceptor

import com.app.server.common.annotation.UserId
import com.app.server.common.exception.UnauthorizedException
import com.app.server.core.security.enums.SecurityExceptionCode
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class UserIdArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == Long::class.java && parameter.hasParameterAnnotation(
            UserId::class.java
        )
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val userIdObj = webRequest.getAttribute("USER_ID", NativeWebRequest.SCOPE_REQUEST)
        if (userIdObj == null) {
            throw UnauthorizedException(SecurityExceptionCode.ACCESS_DENIED_ERROR)
        }
        return userIdObj.toString().toLong()
    }
}
