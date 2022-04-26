package com.github.akhpkn.pdp.domain.task.model

import java.time.Instant
import java.util.UUID

data class Task(
    val id: UUID,
    val title: String,
    val description: String,
    val acceptanceCriteria: String,
    val planId: UUID,
    val status: TaskStatus,
    val createDt: Instant,
    val dueTo: Instant
)
