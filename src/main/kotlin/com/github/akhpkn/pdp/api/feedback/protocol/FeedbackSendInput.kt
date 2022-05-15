package com.github.akhpkn.pdp.api.feedback.protocol

import java.util.UUID

data class FeedbackSendInput(val requestId: UUID, val text: String)
