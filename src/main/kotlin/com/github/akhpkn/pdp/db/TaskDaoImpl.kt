package com.github.akhpkn.pdp.db

import com.github.akhpkn.pdp.domain.task.dao.TaskDao
import com.github.akhpkn.pdp.domain.task.model.Task
import com.github.akhpkn.pdp.domain.task.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class TaskDaoImpl(private val databaseClient: DatabaseClient) : TaskDao {

    override suspend fun find(id: UUID): Task? = run {
        databaseClient
            .sql("select * from task where id=:id")
            .bind("id", id)
            .map(MappingFunctions.toTask)
            .first()
            .awaitSingleOrNull()
    }

    override suspend fun findByIdAndUser(id: UUID, userId: UUID): Task? = run {
        databaseClient
            .sql(
                """
                    |select t.id, t.title, t.description, t.plan_id, t.status 
                    |from task as t
                    |join plan as p on t.plan_id=p.id 
                    |where t.id=:taskId and p.user_id=:userId
                """.trimMargin()
            )
            .bind("taskId", id)
            .bind("userId", userId)
            .map(MappingFunctions.toTask)
            .first()
            .awaitSingleOrNull()
    }

    override fun listByPlan(planId: UUID): Flow<Task> = run {
        databaseClient
            .sql("select * from task where plan_id=:planId order by create_dt")
            .bind("planId", planId)
            .map(MappingFunctions.toTask)
            .all()
            .asFlow()
    }

    override suspend fun insert(task: Task) {
        databaseClient
            .sql(
                """insert into task(id, title, description, acceptance_criteria, plan_id, status, create_dt, due_to) 
                    |values (:id, :title, :description, :acceptanceCriteria, :planId, :status, :createDt, :dueTo)""".trimMargin()
            )
            .bind("id", task.id)
            .bind("title", task.title)
            .bind("description", task.description)
            .bind("acceptanceCriteria", task.acceptanceCriteria)
            .bind("planId", task.planId)
            .bind("status", task.status.name)
            .bind("createDt", task.createDt)
            .bind("dueTo", task.dueTo)
            .await()
    }

    override suspend fun update(id: UUID, status: TaskStatus) {
        databaseClient
            .sql("update task set status=:status where id=:id")
            .bind("id", id)
            .bind("status", status.name)
            .await()
    }

    override suspend fun update(id: UUID, title: String, description: String, acceptanceCriteria: String) {
        databaseClient
            .sql("update task set title=:title, description=:description, acceptance_criteria=:acceptanceCriteria where id=:id")
            .bind("id", id)
            .bind("title", title)
            .bind("description", description)
            .bind("acceptanceCriteria", acceptanceCriteria)
            .await()
    }

    override suspend fun delete(id: UUID) {
        databaseClient
            .sql("delete from task where id=:id")
            .bind("id", id)
            .await()
    }
}
