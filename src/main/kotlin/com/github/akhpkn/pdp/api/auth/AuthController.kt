package com.github.akhpkn.pdp.api.auth

import com.github.akhpkn.pdp.api.auth.protocol.SignInRequest
import com.github.akhpkn.pdp.api.auth.protocol.SignUpRequest
import com.github.akhpkn.pdp.auth.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/signup")
    suspend fun signUp(@RequestBody request: SignUpRequest) = authService.registerUser(request)

    @PostMapping("/signin")
    suspend fun signIn(@RequestBody request: SignInRequest) = authService.authenticateUser(request)
}
