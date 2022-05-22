package com.github.akhpkn.pdp.domain.feedback.model

import java.time.Instant
import java.util.UUID

data class TaskFeedbackView(
    val id: UUID,
    val text: String,
    val createDt: Instant,
    val authorId: UUID,
    val authorEmail: String,
    val authorName: String,
    val authorSurname: String
)
