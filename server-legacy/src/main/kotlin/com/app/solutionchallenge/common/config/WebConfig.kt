package com.app.solutionchallenge.common.config

import com.app.solutionchallenge.common.constant.Constants
import com.app.solutionchallenge.common.interceptor.UserIdArgumentResolver
import com.app.solutionchallenge.common.interceptor.UserIdInterceptor
import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
open class WebConfig(
    private val userIdArgumentResolver: UserIdArgumentResolver,
    private val userIdInterceptor: UserIdInterceptor
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        super.addArgumentResolvers(resolvers)
        resolvers.add(userIdArgumentResolver!!)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(this.userIdInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns(Constants.NO_NEED_AUTH_URLS)
            .excludePathPatterns(Constants.BYPASS_URLS)
    }
}
