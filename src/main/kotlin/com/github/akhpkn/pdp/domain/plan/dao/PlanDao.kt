package com.github.akhpkn.pdp.domain.plan.dao

import com.github.akhpkn.pdp.domain.plan.model.Plan
import com.github.akhpkn.pdp.domain.plan.model.PlanData
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface PlanDao {

    suspend fun find(id: UUID): Plan?

    suspend fun findByIdAndUser(id: UUID, userId: UUID): Plan?

    fun listByAuthor(userId: UUID): Flow<Plan>

    fun listShared(userId: UUID): Flow<PlanData>

    suspend fun update(id: UUID, title: String)

    suspend fun insert(plan: Plan)

    suspend fun delete(id: UUID)
}
