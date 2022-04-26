package com.github.akhpkn.pdp.auth

import com.github.akhpkn.pdp.security.userdetails.CustomUserDetails
import kotlinx.coroutines.reactive.awaitFirst
import mu.KLogging
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import java.util.UUID

object AuthContext : KLogging() {

    suspend fun userId(): UUID = run {
        val userDetails = ReactiveSecurityContextHolder.getContext()
            .awaitFirst()
            .authentication
            .principal as CustomUserDetails

        logger.info { "User details: $userDetails" }

        userDetails.id
    }
}
