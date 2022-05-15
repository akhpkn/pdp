package com.github.akhpkn.pdp.api.plan.protocol

import com.github.akhpkn.pdp.security.AccessType
import java.util.UUID

data class PlanAccessMultipleCreationRequest(
    val planId: UUID,
    val userIds: List<UUID>,
    val type: AccessType
)
