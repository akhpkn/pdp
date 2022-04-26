package com.github.akhpkn.pdp.domain.plan.model

import java.time.Instant
import java.util.UUID

data class PlanData(
    val id: UUID,
    val title: String,
    val userId: UUID,
    val userEmail: String,
    val userName: String,
    val userSurname: String,
    val createDt: Instant,
    val dueTo: Instant
)
