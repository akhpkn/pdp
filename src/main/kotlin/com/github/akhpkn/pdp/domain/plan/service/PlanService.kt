package com.github.akhpkn.pdp.domain.plan.service

import com.github.akhpkn.pdp.domain.plan.dao.PlanAccessDao
import com.github.akhpkn.pdp.domain.plan.dao.PlanDao
import com.github.akhpkn.pdp.domain.plan.exception.PlanNotFoundException
import com.github.akhpkn.pdp.domain.plan.model.Plan
import com.github.akhpkn.pdp.domain.plan.model.PlanAccess
import com.github.akhpkn.pdp.domain.plan.model.PlanData
import com.github.akhpkn.pdp.security.AccessType
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class PlanService(
    private val dao: PlanDao,
    private val accessDao: PlanAccessDao
) {

    @Transactional
    suspend fun create(title: String, userId: UUID, dueTo: Instant) {
        val plan = Plan.new(title, userId, dueTo)
        dao.insert(plan)
        val access = PlanAccess(plan.id, userId, AccessType.Owner)
        accessDao.insert(access)
    }

    suspend fun updateTitle(id: UUID, title: String) {
        val plan = getById(id)
        dao.update(plan.id, title)
    }

    suspend fun delete(id: UUID) {
        val plan = getById(id)
        dao.delete(plan.id)
    }

    fun getByAuthor(userId: UUID): Flow<Plan> = dao.listByAuthor(userId)

    suspend fun getById(id: UUID): Plan = dao.find(id) ?: throw PlanNotFoundException()

    fun getSharedPlans(userId: UUID): Flow<PlanData> = dao.listShared(userId)
}
