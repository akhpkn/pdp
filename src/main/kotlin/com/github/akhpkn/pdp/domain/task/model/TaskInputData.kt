package com.github.akhpkn.pdp.domain.task.model

import java.time.Instant
import java.util.UUID

data class TaskInputData(
    val title: String,
    val description: String,
    val acceptanceCriteria: String,
    val planId: UUID,
    val dueTo: Instant
)
