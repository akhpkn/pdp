package com.github.akhpkn.pdp.api.plan.protocol

import com.github.akhpkn.pdp.security.AccessType
import java.util.UUID

data class PlanAccessCreationRequest(
    val planId: UUID,
    val userId: UUID,
    val type: AccessType
)
