package com.github.akhpkn.pdp.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "jwt")
@ConstructorBinding
data class JwtProperties(
    val secret: String,
    val expirationInMs: Long
)
