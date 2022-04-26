package com.github.akhpkn.pdp.api.plan.protocol

import java.util.UUID

data class PlanAccessDeletionRequest(
    val planId: UUID,
    val userId: UUID
)
