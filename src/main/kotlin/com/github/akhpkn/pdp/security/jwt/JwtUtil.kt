package com.github.akhpkn.pdp.security.jwt

import com.github.akhpkn.pdp.security.userdetails.CustomUserDetails
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.Date
import java.util.UUID

@Component
class JwtUtil(private val properties: JwtProperties) {

    fun generateToken(authentication: Authentication): String {
        val details = authentication.principal as CustomUserDetails

        val now = Date()
        val expiration = Date(now.time + properties.expirationInMs)

        return Jwts.builder().run {
            setSubject(details.id.toString())
            setIssuedAt(now)
            setExpiration(expiration)
            signWith(SignatureAlgorithm.HS512, properties.secret)
            compact()
        }
    }

    fun generateToken(userId: UUID): String {
        val now = Date()
        val expiration = Date(now.time + properties.expirationInMs)

        return Jwts.builder().run {
            setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, properties.secret)
                .compact()
        }
    }

    fun extractUserId(jwt: String): UUID = getClaims(jwt)
        .subject
        .let { UUID.fromString(it) }

    fun validateToken(jwt: String) = runCatching { getClaims(jwt) }.isSuccess

    private fun getClaims(jwt: String) = Jwts.parser()
        .setSigningKey(properties.secret)
        .parseClaimsJws(jwt)
        .body
}
