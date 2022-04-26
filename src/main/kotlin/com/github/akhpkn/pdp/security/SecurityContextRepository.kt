package com.github.akhpkn.pdp.security

import com.github.akhpkn.pdp.security.jwt.JwtUtil
import com.github.akhpkn.pdp.security.userdetails.CustomUserDetailsService
import mu.KLogging
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class SecurityContextRepository(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val jwtUtil: JwtUtil
) : ServerSecurityContextRepository {

    override fun save(exchange: ServerWebExchange?, context: SecurityContext?): Mono<Void> {
        throw UnsupportedOperationException()
    }

    override fun load(exchange: ServerWebExchange?): Mono<SecurityContext> = run {
        logger.info { "Trying to get header" }
        exchange!!.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
            ?.takeIf { it.startsWith("Bearer ") }
            ?.let { value ->
                val token = value.substring(7)
                logger.info { "Got token: $token" }

                val userId = jwtUtil.extractUserId(token)

                logger.info { "UserId = $userId" }

                userDetailsService.findById(userId)
                    .map { UsernamePasswordAuthenticationToken(it, token) }
                    .flatMap { authenticationManager.authenticate(it) }
                    .map { SecurityContextImpl(it) }
            }
            ?: Mono.empty<SecurityContext?>().also {
                logger.info { "Header not found" }
            }
    }

    companion object : KLogging()
}
