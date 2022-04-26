package com.github.akhpkn.pdp.api.auth.protocol

data class SignUpRequest(
    val email: String,
    val password: String,
    val name: String,
    val surname: String
)
