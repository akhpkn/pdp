package com.github.akhpkn.pdp.api.feedback.protocol

import java.util.UUID

data class FeedbackRequestInput(val assigneeId: UUID, val taskId: UUID)
