package com.github.akhpkn.pdp.api.comment.protocol

import com.github.akhpkn.pdp.domain.comment.model.Comment
import java.time.Instant
import java.util.UUID

data class CommentDto(
    val id: UUID,
    val text: String,
    val taskId: UUID,
    val userId: UUID,
    val createDt: Instant,
    val updateDt: Instant,
    val readOnly: Boolean
) {

    companion object {

        fun from(comment: Comment, otherUserId: UUID): CommentDto {
            return with(comment) {
                CommentDto(
                    id = id,
                    text = text,
                    taskId = taskId,
                    userId = userId,
                    createDt = createDt,
                    updateDt = updateDt,
                    readOnly = otherUserId != userId
                )
            }
        }
    }
}
