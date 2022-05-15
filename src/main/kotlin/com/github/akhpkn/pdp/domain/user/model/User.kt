package com.github.akhpkn.pdp.domain.user.model

import java.util.UUID

data class User(
    val id: UUID,
    val email: String,
    val name: String,
    val surname: String
)
