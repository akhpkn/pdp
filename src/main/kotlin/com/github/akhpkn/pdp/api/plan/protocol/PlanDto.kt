package com.github.akhpkn.pdp.api.plan.protocol

import java.time.Instant
import java.util.UUID

data class PlanDto(
    val id: UUID,
    val title: String,
    val userId: UUID,
    val createDt: Instant,
    val dueTo: Instant,
    val tasksCompleted: Int,
    val tasksInProgress: Int,
    val tasksTotal: Int
)
