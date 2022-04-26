package com.github.akhpkn.pdp.domain.task.dao

import com.github.akhpkn.pdp.domain.task.model.Task
import com.github.akhpkn.pdp.domain.task.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TaskDao {

    suspend fun find(id: UUID): Task?

    suspend fun findByIdAndUser(id: UUID, userId: UUID): Task?

    fun listByPlan(planId: UUID): Flow<Task>

    suspend fun insert(task: Task)

    suspend fun update(id: UUID, status: TaskStatus)

    suspend fun update(id: UUID, title: String, description: String, acceptanceCriteria: String)

    suspend fun delete(id: UUID)
}
