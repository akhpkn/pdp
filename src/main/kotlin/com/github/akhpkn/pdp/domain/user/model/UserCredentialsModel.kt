package com.github.akhpkn.pdp.domain.user.model

import java.util.UUID

data class UserCredentialsModel(
    val userId: UUID,
    val password: String
)
