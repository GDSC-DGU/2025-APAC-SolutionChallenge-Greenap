package com.app.server.core.security.filter

import com.app.server.common.enums.CommonResultCode
import com.app.server.common.enums.ResultCode
import com.app.server.common.exception.BadRequestException
import com.app.server.common.response.ApiResponse.Companion.failure
import com.app.server.common.security.enums.SecurityExceptionCode
import com.app.server.user.exception.UserExceptionCode
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.*
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


class JwtExceptionFilter : OncePerRequestFilter() {
    private val objectMapper = ObjectMapper()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: SecurityException) {
            e.printStackTrace()
            logger.error("FilterException throw SecurityException Exception : " + e.message)
            setErrorResponse(response, SecurityExceptionCode.ACCESS_DENIED_ERROR)

            return
        } catch (e: MalformedJwtException) {
            e.printStackTrace()
            logger.error("FilterException throw MalformedJwtException Exception : " + e.message)
            setErrorResponse(response, SecurityExceptionCode.TOKEN_MALFORMED_ERROR)

            return
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            logger.error("FilterException throw IllegalArgumentException Exception : " + e.message)
            setErrorResponse(response, SecurityExceptionCode.TOKEN_TYPE_ERROR)

            return
        } catch (e: ExpiredJwtException) {
            e.printStackTrace()
            logger.error("FilterException throw ExpiredJwtException Exception : " + e.message)
            setErrorResponse(response, SecurityExceptionCode.EXPIRED_TOKEN_ERROR)

            return
        } catch (e: UnsupportedJwtException) {
            e.printStackTrace()
            logger.error("FilterException throw UnsupportedJwtException Exception : " + e.message)
            setErrorResponse(response, SecurityExceptionCode.TOKEN_UNSUPPORTED_ERROR)

            return
        } catch (e: JwtException) {
            e.printStackTrace()
            logger.error("FilterException throw JwtException Exception : " + e.message)
            setErrorResponse(response, SecurityExceptionCode.TOKEN_UNKNOWN_ERROR)

            return
        } catch (e: BadRequestException) {
            e.printStackTrace()
            logger.error("FilterException throw UserBadRequestException Exception : " + e.message)
            setErrorResponse(response, CommonResultCode.BAD_REQUEST)

            return
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("FilterException throw Exception Exception : " + e.message)
            setErrorResponse(response, UserExceptionCode.NOT_FOUND_USER)

            return
        }
    }

    private fun setErrorResponse(response: HttpServletResponse, securityCode: ResultCode) {
        response.contentType = "application/json;charset=UTF-8"
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val apiResponse = failure<Any>(securityCode)
        val jsonResponse = objectMapper.writeValueAsString(apiResponse)
        response.writer.write(jsonResponse)
    }
}
