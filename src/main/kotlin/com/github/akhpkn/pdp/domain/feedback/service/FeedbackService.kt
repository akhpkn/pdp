package com.github.akhpkn.pdp.domain.feedback.service

import com.github.akhpkn.pdp.domain.feedback.dao.FeedbackDao
import com.github.akhpkn.pdp.domain.feedback.exception.FeedbackRequestNotFoundException
import com.github.akhpkn.pdp.domain.feedback.model.FeedbackRequest
import com.github.akhpkn.pdp.domain.feedback.model.TaskFeedback
import com.github.akhpkn.pdp.domain.feedback.model.TaskFeedbackView
import com.github.akhpkn.pdp.domain.notification.service.NotificationSender
import com.github.akhpkn.pdp.domain.task.dao.TaskDao
import com.github.akhpkn.pdp.domain.user.dao.UserDao
import com.github.akhpkn.pdp.exception.BadRequestException
import com.github.akhpkn.pdp.exception.NoAccessException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.singleOrNull
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class FeedbackService(
    private val feedbackDao: FeedbackDao,
    private val userDao: UserDao,
    private val taskDao: TaskDao,
    private val notificationSender: NotificationSender
) {

    companion object : KLogging()

    suspend fun findActiveFeedbackRequest(taskId: UUID, assigneeId: UUID): FeedbackRequest? = run {
        feedbackDao.listRequest(taskId, assigneeId)
            .filter {
                logger.info { "Filtering $it" }
                val take = feedbackDao.findByRequest(it.id) == null
                if (take) {
                    logger.info { "Take $it" }
                }
                take
            }
            .singleOrNull()
    }

    fun listFeedbacks(taskId: UUID): Flow<TaskFeedbackView> = feedbackDao.list(taskId)

    @Transactional
    suspend fun request(userId: UUID, assigneeId: UUID, taskId: UUID) {
        val requester = userDao.getUser(userId)
        val assignee = userDao.getUser(assigneeId)
        val task = taskDao.getTask(taskId)

        findActiveFeedbackRequest(taskId, assigneeId)?.let {
            throw BadRequestException("Вы уже запросили обратную связь у ${assignee.name} ${assignee.surname}")
        }

        val request = makeFeedbackRequest(userId, assigneeId, taskId)
        feedbackDao.insert(request)
        notificationSender.notifyFeedbackRequested(
            requester = requester,
            assignee = assignee,
            task = task
        )
    }

    private fun makeFeedbackRequest(userId: UUID, assigneeId: UUID, taskId: UUID): FeedbackRequest {
        return FeedbackRequest(
            id = UUID.randomUUID(),
            requesterId = userId,
            assigneeId = assigneeId,
            taskId = taskId,
            createdDt = Instant.now()
        )
    }

    @Transactional
    suspend fun send(requestId: UUID, userId: UUID, text: String) {
        val request = feedbackDao.findRequest(requestId) ?: throw FeedbackRequestNotFoundException()
        val requester = userDao.getUser(request.requesterId)
        val assignee = userDao.getUser(request.assigneeId)
        val task = taskDao.getTask(request.taskId)
        if (request.assigneeId != userId) throw NoAccessException()

        val feedback = TaskFeedback(
            id = UUID.randomUUID(),
            requestId = requestId,
            text = text,
            createDt = Instant.now()
        )
        feedbackDao.insert(feedback)
        notificationSender.notifyFeedbackReceived(
            requester = requester,
            assignee = assignee,
            task = task
        )
    }
}
