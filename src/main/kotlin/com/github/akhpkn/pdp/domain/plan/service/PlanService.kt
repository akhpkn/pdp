package com.github.akhpkn.pdp.domain.plan.service

import com.github.akhpkn.pdp.domain.plan.dao.PlanAccessDao
import com.github.akhpkn.pdp.domain.plan.dao.PlanDao
import com.github.akhpkn.pdp.security.AccessType
import com.github.akhpkn.pdp.domain.plan.model.Plan
import com.github.akhpkn.pdp.domain.plan.model.PlanAccess
import com.github.akhpkn.pdp.domain.plan.model.PlanData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
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

    fun getByAuthor(userId: UUID): Flow<Plan> = dao.listByAuthor(userId)

    suspend fun getById(id: UUID): Plan = dao.find(id) ?: throw RuntimeException("Plan not found")

    fun getSharedPlans(userId: UUID): Flow<PlanData> = dao.listShared(userId)
//        accessDao.listByUser(userId)
//            .filter { it.type != AccessType.Owner }
//            .map { dao.find(it.planId)!! }
}
