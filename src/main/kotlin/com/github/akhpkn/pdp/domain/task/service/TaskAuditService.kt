package com.github.akhpkn.pdp.domain.task.service

import com.github.akhpkn.pdp.domain.task.dao.TaskAuditDao
import com.github.akhpkn.pdp.domain.task.model.TaskAudit
import com.github.akhpkn.pdp.domain.task.model.TaskAuditData
import com.github.akhpkn.pdp.domain.task.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class TaskAuditService(private val dao: TaskAuditDao) {

    fun listByPlan(planId: UUID): Flow<TaskAuditData> = dao.listByPlan(planId)

    suspend fun record(event: AuditEvent) {
        val taskAudit = when (event) {
            is CreationEvent -> makeTaskAudit(event.taskId, TaskStatus.New)
            is StatusUpdateEvent -> makeTaskAudit(event.taskId, event.status)
        }
        dao.insert(taskAudit)
    }

    private fun makeTaskAudit(taskId: UUID, status: TaskStatus) = TaskAudit(taskId, status, Instant.now())
}

sealed class AuditEvent

data class CreationEvent(val taskId: UUID) : AuditEvent()

data class StatusUpdateEvent(val taskId: UUID, val status: TaskStatus) : AuditEvent()
