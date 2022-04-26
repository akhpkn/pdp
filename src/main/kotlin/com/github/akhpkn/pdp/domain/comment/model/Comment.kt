package com.github.akhpkn.pdp.domain.comment.model

import java.time.Instant
import java.util.UUID

data class Comment(
    val id: UUID,
    val text: String,
    val taskId: UUID,
    val userId: UUID,
    val createDt: Instant,
    val updateDt: Instant
) {

    companion object {
        fun new(text: String, userId: UUID, taskId: UUID): Comment {
            return Comment(
                id = UUID.randomUUID(),
                text = text,
                taskId = taskId, userId = userId,
                createDt = Instant.now(), updateDt = Instant.now()
            )
        }
    }
}
