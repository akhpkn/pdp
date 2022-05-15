package com.github.akhpkn.pdp.api.plan.protocol

import java.time.Instant
import java.util.UUID

data class PlanDataDto(
    val id: UUID,
    val title: String,
    val userId: UUID,
    val userEmail: String,
    val userName: String,
    val userSurname: String,
    val createDt: Instant,
    val dueTo: Instant,
    val tasksCompleted: Int,
    val tasksInProgress: Int,
    val tasksTotal: Int
)
