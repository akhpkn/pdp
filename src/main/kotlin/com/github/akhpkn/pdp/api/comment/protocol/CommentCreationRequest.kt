package com.github.akhpkn.pdp.api.comment.protocol

import java.util.UUID

data class CommentCreationRequest(
    val text: String,
    val taskId: UUID,
)
