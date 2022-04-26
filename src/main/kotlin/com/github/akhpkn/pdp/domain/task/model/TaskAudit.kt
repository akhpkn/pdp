package com.github.akhpkn.pdp.domain.task.model

import java.time.Instant
import java.util.UUID

data class TaskAudit(
    val taskId: UUID,
    val status: TaskStatus,
    val dateTime: Instant
)

data class  TaskAuditData(
    val taskId: UUID,
    val taskTitle: String,
    val status: TaskStatus,
    val dateTime: Instant,
)
