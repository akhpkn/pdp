package com.github.akhpkn.pdp.domain.comment.model

import java.time.Instant
import java.util.UUID

data class CommentData(
    val id: UUID,
    val text: String,
    val userId: UUID,
    val userName: String,
    val userSurname: String,
    val userEmail: String,
    val createDt: Instant
)
