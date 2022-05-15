package com.github.akhpkn.pdp.db

import com.github.akhpkn.pdp.domain.task.dao.TaskAuditDao
import com.github.akhpkn.pdp.domain.task.model.TaskAudit
import com.github.akhpkn.pdp.domain.task.model.TaskAuditData
import kotlinx.coroutines.flow.Flow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class TaskAuditDaoImpl(private val databaseClient: DatabaseClient) : TaskAuditDao {


    override fun listByPlan(planId: UUID): Flow<TaskAuditData> = run {
        databaseClient
            .sql(
                """
                select t.id as task_id, t.title as task_title, a.status as status, a.date_time as date_time
                from task t 
                join task_audit a on t.id=a.task_id
                where t.plan_id=:planId
                order by a.date_time
            """.trimIndent()
            )
            .bind("planId", planId)
            .map(MappingFunctions.toTaskAuditData)
            .flow()
    }

    override suspend fun insert(taskAudit: TaskAudit) {
        databaseClient
            .sql("insert into task_audit(task_id, status, date_time) values (:taskId, :status, :dateTime)")
            .bind("taskId", taskAudit.taskId)
            .bind("status", taskAudit.status.name)
            .bind("dateTime", taskAudit.dateTime)
            .await()
    }
}
