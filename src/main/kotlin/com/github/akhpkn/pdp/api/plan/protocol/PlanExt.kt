package com.github.akhpkn.pdp.api.plan.protocol

import com.github.akhpkn.pdp.domain.plan.model.Plan
import com.github.akhpkn.pdp.security.AccessType
import java.time.Instant
import java.util.UUID

data class PlanExt(
    val id: UUID,
    val title: String,
    val userId: UUID,
    val createDt: Instant,
    val dueTo: Instant,
    val accessType: AccessType
) {

    companion object {
        fun from(plan: Plan, accessType: AccessType) = PlanExt(
            id = plan.id,
            title = plan.title,
            userId = plan.userId,
            createDt = plan.createDt,
            dueTo = plan.dueTo,
            accessType = accessType
        )
    }
}
