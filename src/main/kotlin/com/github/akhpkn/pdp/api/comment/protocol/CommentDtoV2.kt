package com.github.akhpkn.pdp.api.comment.protocol

import com.github.akhpkn.pdp.domain.comment.model.CommentData
import java.time.Instant
import java.util.UUID

data class CommentDtoV2(
    val id: UUID,
    val text: String,
    val userId: UUID,
    val userName: String,
    val userSurname: String,
    val userEmail: String,
    val createDt: Instant,
    val readOnly: Boolean
) {

    companion object {
        fun from(commentData: CommentData, otherUserId: UUID): CommentDtoV2 = with(commentData) {
            CommentDtoV2(
                id = id,
                text = text,
                userId = userId,
                userName = userName,
                userSurname = userSurname,
                userEmail = userEmail,
                createDt = createDt,
                readOnly = userId != otherUserId
            )
        }
    }
}
