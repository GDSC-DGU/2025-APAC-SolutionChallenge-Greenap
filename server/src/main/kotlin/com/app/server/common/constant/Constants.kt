package com.app.server.common.constant


object Constants {
    const val BEARER_PREFIX: String = "Bearer "
    const val AUTHORIZATION_HEADER: String = "Authorization"
    const val REAUTHORIZATION: String = "refreshToken"
    const val ROLE_PREFIX: String = "ROLE_"
    const val USER_ROLE_CLAIM_NAME: String = "role"
    const val USER_ID_CLAIM_NAME: String = "uid"
    const val USER_EMAIL_CLAIM_NAME: String = "email"

    /**
     * Urls which don't need authentication
     * but need to be filtered
     *
     * @see JwtAuthenticationFilter
     */
    
    val NO_NEED_AUTH_URLS: List<String> = listOf( // 회원가입
        "/api/auth/sign-up",  // 로그인
        "/api/auth/sign-in",
        "/api/auth/login/**",
        "/api/auth/login/kakao",
        "/api/auth/login/naver",
        "/api/auth/login/google",
        "/api/auth/login/apple",
        "/oauth2/authorization/google",
        "/login/oauth2/code/google",
        "/favicon.ico",
    )

    /**
     * Urls which bypass security filter so that it can be accessed without authentication
     * Difference from NO_NEED_AUTH_URLS, NO_NEED_AUTH_URLS is
     * that they don't need to be authenticated but need to be filtered.
     *
     * @see JwtAuthenticationFilter
     */
    
    val BYPASS_URLS: List<String> = listOf( //
        "/hello",  // 모니터링
        "/actuator/**",  // 피드백 데이터 조회
    )

    const val CONTENT_TYPE: String = "Content-Type"
}
