package com.github.akhpkn.pdp.domain.task.service

import com.github.akhpkn.pdp.domain.task.dao.TaskDao
import com.github.akhpkn.pdp.domain.task.exception.TaskNotFoundException
import com.github.akhpkn.pdp.domain.task.model.Task
import com.github.akhpkn.pdp.domain.task.model.TaskInputData
import com.github.akhpkn.pdp.domain.task.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class TaskService(
    private val dao: TaskDao,
    private val auditService: TaskAuditService
) {

    suspend fun getById(id: UUID): Task = dao.find(id) ?: throw TaskNotFoundException()

    fun getByPlanId(planId: UUID): Flow<Task> = dao.listByPlan(planId)

    @Transactional
    suspend fun create(inputData: TaskInputData) {
        val task = makeTask(inputData)
        dao.insert(task)
        auditService.record(CreationEvent(task.id))
    }

    private fun makeTask(inputData: TaskInputData): Task =
        Task(
            id = UUID.randomUUID(),
            title = inputData.title,
            description = inputData.description,
            acceptanceCriteria = inputData.acceptanceCriteria,
            planId = inputData.planId,
            status = TaskStatus.New,
            createDt = Instant.now(),
            dueTo = inputData.dueTo
        )

    @Transactional
    suspend fun updateStatus(id: UUID, status: TaskStatus) {
        val task = getById(id)
        dao.update(task.id, status)
        auditService.record(StatusUpdateEvent(task.id, status))
    }

    suspend fun updateInfo(id: UUID, title: String, description: String, acceptanceCriteria: String) {
        val task = getById(id)
        dao.update(task.id, title, description, acceptanceCriteria)
    }

    suspend fun delete(id: UUID) {
        val task = getById(id)
        dao.delete(task.id)
    }
}
