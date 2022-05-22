package com.github.akhpkn.pdp.domain.feedback.model

import java.time.Instant
import java.util.UUID

data class FeedbackRequest(
    val id: UUID,
    val requesterId: UUID,
    val assigneeId: UUID,
    val taskId: UUID,
    val createdDt: Instant
)
