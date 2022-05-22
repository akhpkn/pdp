package com.github.akhpkn.pdp.domain.feedback.model

import java.time.Instant
import java.util.UUID

data class TaskFeedback(
    val id: UUID,
    val requestId: UUID,
    val text: String,
    val createDt: Instant,
)

