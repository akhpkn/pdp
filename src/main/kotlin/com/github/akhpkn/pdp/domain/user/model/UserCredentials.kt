package com.github.akhpkn.pdp.domain.user.model

import java.util.UUID

data class UserCredentials(
    val userId: UUID,
    val email: String,
    val password: String
)
