package com.github.akhpkn.pdp.domain.plan.model

import com.github.akhpkn.pdp.security.AccessType
import java.util.UUID

data class PlanAccess(
    val planId: UUID,
    val userId: UUID,
    val type: AccessType
)

data class PlanAccessInfo(
    val planId: UUID,
    val userId: UUID,
    val userEmail: String,
    val userName: String,
    val userSurname: String,
    val type: AccessType
)
