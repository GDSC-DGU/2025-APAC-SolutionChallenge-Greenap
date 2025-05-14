package com.app.server.user.ui.controller

import com.app.server.user.application.service.UserCommandService
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/user")
class UserController (
    private val userService: UserCommandService
){

    //TODO: 이름 변경, 사진 변경
    @PatchMapping("/nickname")
    fun updateNickname(
        @RequestParam nickname: String
    ) {
    }

    @PatchMapping("/profile-image")
    fun updateProfile(
        @RequestPart image: MultipartFile
    ) {
    }
}