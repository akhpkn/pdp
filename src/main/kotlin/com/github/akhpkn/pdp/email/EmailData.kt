package com.github.akhpkn.pdp.email

data class EmailData(
    val receiver: String,
    val subject: String,
    val text: String
)
