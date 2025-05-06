package com.app.server.core.security.config

import com.app.server.common.constant.Constants
import com.app.server.core.security.JwtAuthEntryPoint
import com.app.server.core.security.filter.CustomLogoutFilter
import com.app.server.core.security.filter.JwtAuthenticationFilter
import com.app.server.core.security.filter.JwtExceptionFilter
import com.app.server.core.security.handler.CustomSignOutProcessHandler
import com.app.server.core.security.handler.CustomSignOutResultHandler
import com.app.server.core.security.handler.JwtAccessDeniedHandler
import com.app.server.core.security.provider.JwtAuthenticationProvider
import com.app.server.core.security.service.CustomUserDetailsService
import com.app.server.core.security.util.JwtUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
class SecurityConfig(
    private val jwtUtil: JwtUtil,
    private val customUserDetailsService: CustomUserDetailsService,
    private val customSignOutProcessHandler: CustomSignOutProcessHandler,
    private val customSignOutResultHandler: CustomSignOutResultHandler,
    private val jwtAuthEntryPoint: JwtAuthEntryPoint,
    private val jwtAccessDeniedHandler: JwtAccessDeniedHandler
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(*Constants.NO_NEED_AUTH_URLS.toTypedArray()).permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .formLogin { it.disable() }
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint(jwtAuthEntryPoint)
                    .accessDeniedHandler(jwtAccessDeniedHandler)
            }
            .logout { logout ->
                logout.logoutUrl("/api/auth/sign-out")
                    .addLogoutHandler(customSignOutProcessHandler)
                    .logoutSuccessHandler(customSignOutResultHandler)
                    .deleteCookies(Constants.AUTHORIZATION_HEADER, Constants.REAUTHORIZATION)
            }
            .addFilterBefore(CustomLogoutFilter(), LogoutFilter::class.java)
            .addFilterBefore(
                JwtAuthenticationFilter(jwtUtil, JwtAuthenticationProvider(customUserDetailsService)),
                CustomLogoutFilter::class.java
            )
            .addFilterBefore(JwtExceptionFilter(), JwtAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer =
        WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(*Constants.BYPASS_URLS.toTypedArray())
        }
}