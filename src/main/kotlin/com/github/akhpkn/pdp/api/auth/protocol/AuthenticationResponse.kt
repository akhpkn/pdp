package com.github.akhpkn.pdp.api.auth.protocol

data class AuthenticationResponse(val token: String) {
    val type: String = "Bearer"
}
