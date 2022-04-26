package com.github.akhpkn.pdp.security

import com.github.akhpkn.pdp.security.jwt.JwtUtil
import kotlinx.coroutines.reactor.mono
import mu.KLogging
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AuthenticationManager(private val jwtUtil: JwtUtil) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication?): Mono<Authentication> = mono {
        logger.info { "Getting auth token" }
        val authToken = authentication!!.credentials.toString()

        logger.info { "auth token: $authToken" }

        jwtUtil.validateToken(authToken)
            .takeIf { it }
            ?.let { makeAuthentication(authentication) }
    }

    private fun makeAuthentication(authentication: Authentication) =
        UsernamePasswordAuthenticationToken(
            authentication.principal,
            null,
            mutableListOf()
        )

    companion object : KLogging()
}
