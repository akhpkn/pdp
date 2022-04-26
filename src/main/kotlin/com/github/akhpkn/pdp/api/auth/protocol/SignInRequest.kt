package com.github.akhpkn.pdp.api.auth.protocol

data class SignInRequest(
    val email: String,
    val password: String
)
