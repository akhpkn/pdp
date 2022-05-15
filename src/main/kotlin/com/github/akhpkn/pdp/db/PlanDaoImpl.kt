package com.github.akhpkn.pdp.db

import com.github.akhpkn.pdp.domain.plan.dao.PlanDao
import com.github.akhpkn.pdp.domain.plan.model.Plan
import com.github.akhpkn.pdp.domain.plan.model.PlanData
import kotlinx.coroutines.flow.Flow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.awaitSingleOrNull
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PlanDaoImpl(private val databaseClient: DatabaseClient): PlanDao {

    override suspend fun find(id: UUID): Plan? = run {
        databaseClient
            .sql("select * from plan where id=:id")
            .bind("id", id)
            .map(MappingFunctions.toPlan)
            .awaitSingleOrNull()
    }

    override suspend fun findByIdAndUser(id: UUID, userId: UUID): Plan? = run {
        databaseClient
            .sql("select * from plan where id=:planId and user_id=:userId")
            .bind("planId", id)
            .bind("userId", userId)
            .map(MappingFunctions.toPlan)
            .awaitSingleOrNull()
    }

    override fun listByAuthor(userId: UUID): Flow<Plan> = run {
        databaseClient
            .sql("select * from plan where user_id=:userId")
            .bind("userId", userId)
            .map(MappingFunctions.toPlan)
            .flow()
    }

    override fun listShared(userId: UUID): Flow<PlanData> = run {
        databaseClient
            .sql(
                """
                    select p.id as id, p.title as title, p.create_dt as create_dt, p.due_to as due_to,
                    u.id as user_id, u.email as user_email, u.name as user_name, u.surname as user_surname
                    from plan p
                    join "user" u on p.user_id=u.id
                    join plan_access pa on p.id = pa.plan_id
                    where pa.user_id=:userId and pa.type!='Owner'
                """.trimIndent()
            )
            .bind("userId", userId)
            .map(MappingFunctions.toPlanData)
            .flow()
    }

    override suspend fun update(id: UUID, title: String) {
        databaseClient
            .sql("update plan set title=:title where id=:id")
            .bind("id", id)
            .bind("title", title)
            .await()
    }

    override suspend fun insert(plan: Plan) {
        databaseClient
            .sql("insert into plan(id, title, user_id, create_dt, due_to) values (:id, :title, :userId, :createDt, :dueTo)")
            .bind("id", plan.id)
            .bind("title", plan.title)
            .bind("userId", plan.userId)
            .bind("createDt", plan.createDt)
            .bind("dueTo", plan.dueTo)
            .await()
    }

    override suspend fun delete(id: UUID) {
        databaseClient
            .sql("delete from plan where id=:id")
            .bind("id", id)
            .await()
    }
}
