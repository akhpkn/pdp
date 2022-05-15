package com.github.akhpkn.pdp.domain.plan.service

import com.github.akhpkn.pdp.domain.plan.dao.PlanAccessDao
import com.github.akhpkn.pdp.domain.plan.model.PlanAccess
import com.github.akhpkn.pdp.security.AccessType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PlanAccessService(private val dao: PlanAccessDao) {

    fun listUsersWithPlanAccess(planId: UUID) = dao.listByPlanJoinUser(planId)

    suspend fun grant(planId: UUID, userId: UUID, accessType: AccessType) {
        val current = dao.find(planId, userId)
        if (current == null) {
            dao.insert(PlanAccess(planId, userId, accessType))
        } else if (current.type != accessType) {
            dao.update(planId, userId, accessType)
        }
    }

    suspend fun grant(planId: UUID, userIds: List<UUID>, accessType: AccessType) {
        userIds.forEach {
            grant(planId, it, accessType)
        }
    }

    suspend fun delete(planId: UUID, userId: UUID) {
        dao.find(planId, userId)
            ?.let { dao.delete(planId, userId) }
    }
}
