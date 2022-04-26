package com.github.akhpkn.pdp.domain.plan.dao

import com.github.akhpkn.pdp.security.AccessType
import com.github.akhpkn.pdp.domain.plan.model.PlanAccess
import com.github.akhpkn.pdp.domain.plan.model.PlanAccessInfo
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface PlanAccessDao {

    fun listByUser(userId: UUID): Flow<PlanAccess>

    fun listByPlan(planId: UUID): Flow<PlanAccess>

    fun listByPlanJoinUser(planId: UUID): Flow<PlanAccessInfo>

    suspend fun find(planId: UUID, userId: UUID): PlanAccess?

    suspend fun findByTaskAndUser(taskId: UUID, userId: UUID): PlanAccess?

    suspend fun insert(planAccess: PlanAccess)

    suspend fun update(planId: UUID, userId: UUID, type: AccessType)

    suspend fun delete(planId: UUID, userId: UUID)
}
