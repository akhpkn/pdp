package com.github.akhpkn.pdp.api.task.protocol

import java.time.Instant
import java.util.UUID

data class TaskCreationRequest(
    val title: String,
    val description: String,
    val acceptanceCriteria: String,
    val planId: UUID,
    val dueTo: Instant
)
