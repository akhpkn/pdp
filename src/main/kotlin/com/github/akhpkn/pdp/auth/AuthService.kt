package com.github.akhpkn.pdp.auth

import com.github.akhpkn.pdp.api.auth.protocol.AuthenticationResponse
import com.github.akhpkn.pdp.api.auth.protocol.SignInRequest
import com.github.akhpkn.pdp.api.auth.protocol.SignUpRequest
import com.github.akhpkn.pdp.domain.user.dao.UserCredentialsDao
import com.github.akhpkn.pdp.domain.user.model.UserCredentials
import com.github.akhpkn.pdp.domain.user.model.UserCredentialsModel
import com.github.akhpkn.pdp.domain.user.service.UserService
import com.github.akhpkn.pdp.security.AuthenticationManager
import com.github.akhpkn.pdp.security.jwt.JwtUtil
import kotlinx.coroutines.reactive.awaitFirst
import mu.KLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AuthService(
    private val userService: UserService,
    private val userCredentialsDao: UserCredentialsDao,
    private val passwordEncoder: PasswordEncoder,
//    private val custom
//    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil
) {

    @Transactional
    suspend fun registerUser(request: SignUpRequest): AuthenticationResponse {
        if (userService.existsByEmail(request.email)) {
            throw RuntimeException("User exists by email!")
        }

        val password = passwordEncoder.encode(request.password)
        val userId = userService.createUser(request.email, request.name, request.surname)
        userCredentialsDao.insert(UserCredentialsModel(userId, password))

        logger.info { "Trying to authenticate" }

        return doAuthenticate(request.email, request.password)
    }

    suspend fun authenticateUser(request: SignInRequest) = doAuthenticate(request.email, request.password)

    private suspend fun doAuthenticate(email: String, password: String): AuthenticationResponse {
//        val authentication = authenticationManager
//            .authenticate(UsernamePasswordAuthenticationToken(email, password))
//            .awaitFirst()

//        logger.info { "Received authentication" }

//        ReactiveSecurityContextHolder.getContext()
//            .awaitFirst()
//            .authentication = authentication

//        logger.info { "Set authentication to context" }
        logger.info { "Trying to authenticate user by email=$email, password=$password" }
        val user = userCredentialsDao.find(email) ?: throw RuntimeException("User doesn't exist")
        if (passwordEncoder.matches(password, user.password)) {
            val token = jwtUtil.generateToken(user.userId)

            return AuthenticationResponse(token)
        } else {
            throw RuntimeException("Wrong password")
        }
    }

    companion object : KLogging()
}
