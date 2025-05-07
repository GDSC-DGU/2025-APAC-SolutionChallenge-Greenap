package com.app.server.user.ui.controller

import com.app.server.auth.application.service.AuthService
import com.app.server.common.constant.Constants
import com.app.server.common.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController (
    private val authService: AuthService
){

}