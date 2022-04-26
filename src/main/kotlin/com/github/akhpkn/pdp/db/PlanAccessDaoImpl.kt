package com.github.akhpkn.pdp.db

import com.github.akhpkn.pdp.domain.plan.dao.PlanAccessDao
import com.github.akhpkn.pdp.domain.plan.model.PlanAccess
import com.github.akhpkn.pdp.domain.plan.model.PlanAccessInfo
import com.github.akhpkn.pdp.security.AccessType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PlanAccessDaoImpl(private val databaseClient: DatabaseClient) : PlanAccessDao {

    override fun listByUser(userId: UUID): Flow<PlanAccess> = run {
        databaseClient
            .sql("select * from plan_access where user_id=:userId")
            .bind("userId", userId)
            .map(MappingFunctions.toPlanAccess)
            .all()
            .asFlow()
    }

    override fun listByPlan(planId: UUID): Flow<PlanAccess> = run {
        databaseClient
            .sql("select * from plan_access where plan_id=:planId")
            .bind("planId", planId)
            .map(MappingFunctions.toPlanAccess)
            .all()
            .asFlow()
    }

    override fun listByPlanJoinUser(planId: UUID): Flow<PlanAccessInfo> = run {
        databaseClient
            .sql(
                """
                    select pa.plan_id as plan_id, pa.type as type,
                    u.id as user_id, u.email as user_email, u.name as user_name, u.surname as user_surname
                    from plan_access pa
                    join "user" u on u.id = pa.user_id
                    where pa.plan_id=:planId
                """.trimIndent()
            )
            .bind("planId", planId)
            .map(MappingFunctions.toPlanAccessInfo)
            .all()
            .asFlow()
    }

    override suspend fun find(planId: UUID, userId: UUID): PlanAccess? = run {
        databaseClient
            .sql("select * from plan_access where user_id=:userId and plan_id=:planId")
            .bind("planId", planId)
            .bind("userId", userId)
            .map(MappingFunctions.toPlanAccess)
            .first()
            .awaitSingleOrNull()
    }

    override suspend fun findByTaskAndUser(taskId: UUID, userId: UUID): PlanAccess? = run {
        databaseClient
            .sql(
                """
                    |select pa.plan_id, pa.user_id, pa.type
                    |from plan_access as pa
                    |join task as t on t.plan_id=pa.plan_id
                    |where t.id=:taskId and pa.user_id=:userId
                """.trimMargin()
            )
            .bind("taskId", taskId)
            .bind("userId", userId)
            .map(MappingFunctions.toPlanAccess)
            .first()
            .awaitSingleOrNull()
    }

    override suspend fun insert(planAccess: PlanAccess) {
        databaseClient
            .sql("insert into plan_access(plan_id, user_id, type) values (:planId, :userId, :type)")
            .bind("planId", planAccess.planId)
            .bind("userId", planAccess.userId)
            .bind("type", planAccess.type.name)
            .await()
    }

    override suspend fun update(planId: UUID, userId: UUID, type: AccessType) {
        databaseClient
            .sql("update plan_access set type=:type where plan_id=:planId and user_id=:userId")
            .bind("type", type.name)
            .bind("planId", planId)
            .bind("userId", userId)
            .await()
    }

    override suspend fun delete(planId: UUID, userId: UUID) {
        databaseClient
            .sql("delete from plan_access where plan_id=:planId and user_id=:userId")
            .bind("planId", planId)
            .bind("userId", userId)
            .await()
    }
}
