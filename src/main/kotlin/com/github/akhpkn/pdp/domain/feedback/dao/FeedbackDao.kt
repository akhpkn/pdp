package com.github.akhpkn.pdp.domain.feedback.dao

import com.github.akhpkn.pdp.domain.feedback.model.FeedbackRequest
import com.github.akhpkn.pdp.domain.feedback.model.TaskFeedback
import com.github.akhpkn.pdp.domain.feedback.model.TaskFeedbackView
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface FeedbackDao {

    suspend fun find(id: UUID): TaskFeedback?

    suspend fun findByRequest(requestId: UUID): TaskFeedback?

    fun list(taskId: UUID): Flow<TaskFeedbackView>

    suspend fun insert(feedback: TaskFeedback)

    suspend fun findRequest(id: UUID): FeedbackRequest?

    suspend fun findRequest(taskId: UUID, assigneeId: UUID): FeedbackRequest?

    suspend fun listRequest(taskId: UUID, assigneeId: UUID): Flow<FeedbackRequest>

    suspend fun insert(request: FeedbackRequest)
}
