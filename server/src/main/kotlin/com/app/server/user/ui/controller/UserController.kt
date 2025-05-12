package com.app.server.user.ui.controller

import com.app.server.auth.application.service.AuthService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController (
    private val authService: AuthService
){

    //TODO: 이름 변경, 사진 변경
}