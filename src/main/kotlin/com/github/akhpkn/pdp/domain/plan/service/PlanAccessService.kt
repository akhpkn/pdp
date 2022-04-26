package com.github.akhpkn.pdp.domain.plan.service

import com.github.akhpkn.pdp.domain.plan.dao.PlanAccessDao
import com.github.akhpkn.pdp.security.AccessType
import com.github.akhpkn.pdp.domain.plan.model.PlanAccess
import com.github.akhpkn.pdp.domain.plan.model.PlanAccessInfo
import kotlinx.coroutines.flow.Flow
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

    suspend fun delete(planId: UUID, userId: UUID) {
        dao.find(planId, userId)
            ?.let { dao.delete(planId, userId) }
    }
}
