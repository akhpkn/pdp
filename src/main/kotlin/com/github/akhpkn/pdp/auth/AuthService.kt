package com.github.akhpkn.pdp.auth

import com.github.akhpkn.pdp.api.auth.protocol.AuthenticationResponse
import com.github.akhpkn.pdp.api.auth.protocol.SignInRequest
import com.github.akhpkn.pdp.api.auth.protocol.SignUpRequest
import com.github.akhpkn.pdp.domain.notification.service.NotificationSettingsService
import com.github.akhpkn.pdp.domain.user.dao.UserCredentialsDao
import com.github.akhpkn.pdp.domain.user.model.UserCredentialsModel
import com.github.akhpkn.pdp.domain.user.service.UserService
import com.github.akhpkn.pdp.security.jwt.JwtUtil
import mu.KLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class AuthService(
    private val userService: UserService,
    private val userCredentialsDao: UserCredentialsDao,
    private val passwordEncoder: PasswordEncoder,
    private val notificationSettingsService: NotificationSettingsService,
    private val jwtUtil: JwtUtil
) {
    private fun call(signUpRequest: SignUpRequest) = registerUserMono(signUpRequest)

    private fun UserService.existsByEmailMono(email: String): Mono<Boolean> = TODO()
    private fun UserService.createUserMono(email: String, name: String, surname: String): Mono<UUID> = TODO()
    private fun UserCredentialsDao.insertMono(usm: UserCredentialsModel): Mono<Void> = TODO()
    private fun NotificationSettingsService.createDefaultSettingsMono(userId: UUID): Mono<Void> = TODO()
    private fun doAuthenticateMono(email: String, password: String): Mono<AuthenticationResponse> = TODO()

    @Transactional
    fun registerUserMono(request: SignUpRequest): Mono<AuthenticationResponse> {
        return userService.existsByEmailMono(request.email)
            .map { userExists ->
                if (userExists) throw UserExistsByEmailException()
                passwordEncoder.encode(request.password)
            }
            .flatMap { password ->
                userService.createUserMono(request.email, request.name, request.surname)
                    .flatMap { userId ->
                        userCredentialsDao.insertMono(UserCredentialsModel(userId, password))
                            .flatMap { notificationSettingsService.createDefaultSettingsMono(userId) }
                    }
            }
            .flatMap { doAuthenticateMono(request.email, request.password) }
    }

    @Transactional
    suspend fun registerUser(request: SignUpRequest): AuthenticationResponse {
        if (userService.existsByEmail(request.email)) {
            throw UserExistsByEmailException()
        }

        val password = passwordEncoder.encode(request.password)
        val userId = userService.createUser(request.email, request.name, request.surname)
        userCredentialsDao.insert(UserCredentialsModel(userId, password))
        notificationSettingsService.createDefaultSettings(userId)

        return doAuthenticate(request.email, request.password)
    }

    suspend fun authenticateUser(request: SignInRequest) = doAuthenticate(request.email, request.password)

    private suspend fun doAuthenticate(email: String, password: String): AuthenticationResponse {
        val user = userCredentialsDao.find(email) ?: throw UserNotFoundException()

        return if (passwordEncoder.matches(password, user.password)) {
            val token = jwtUtil.generateToken(user.userId)
            AuthenticationResponse(token)
        } else {
            throw WrongPasswordException()
        }
    }

    companion object : KLogging()
}
