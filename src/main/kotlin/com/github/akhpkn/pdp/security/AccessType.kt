package com.github.akhpkn.pdp.security

enum class AccessType {
    Read,
    Write,
    Owner;

    fun covers(other: AccessType): Boolean {
        return when (this) {
            Read -> other == Read
            Write -> other != Owner
            Owner -> true
        }
    }
}
