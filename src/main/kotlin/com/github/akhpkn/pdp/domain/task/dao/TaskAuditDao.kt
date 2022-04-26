package com.github.akhpkn.pdp.domain.task.dao

import com.github.akhpkn.pdp.domain.task.model.TaskAudit
import com.github.akhpkn.pdp.domain.task.model.TaskAuditData
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TaskAuditDao {

    fun listByPlan(planId: UUID): Flow<TaskAuditData>

    suspend fun insert(taskAudit: TaskAudit)
}
