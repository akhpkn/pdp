package com.github.akhpkn.pdp.domain.feedback

import java.time.Instant
import java.util.UUID

data class TaskFeedback(
    val id: UUID,
    val requestId: UUID,
    val text: String,
    val createDt: Instant,
)

data class TaskFeedbackView(
    val id: UUID,
    val text: String,
    val createDt: Instant,
    val authorId: UUID,
    val authorEmail: String,
    val authorName: String,
    val authorSurname: String
)
