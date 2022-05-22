package com.github.akhpkn.pdp.security

import com.github.akhpkn.pdp.domain.comment.dao.CommentDao
import com.github.akhpkn.pdp.domain.comment.model.Comment
import com.github.akhpkn.pdp.domain.plan.dao.PlanAccessDao
import com.github.akhpkn.pdp.exception.NoAccessException
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AccessService(
    private val planAccessDao: PlanAccessDao,
    private val commentDao: CommentDao
) {

    suspend fun checkPlanAccess(userId: UUID, planId: UUID, accessType: AccessType): AccessType = run {
        planAccessDao.find(planId, userId)
            ?.takeIf { it.type.covers(accessType) }
            ?.type
            ?: throw NoAccessException()
    }

    suspend fun checkTaskAccess(userId: UUID, taskId: UUID, accessType: AccessType): AccessType = run {
        planAccessDao.findByTaskAndUser(taskId, userId)
            ?.takeIf { it.type.covers(accessType) }
            ?.type
            ?: throw NoAccessException()
    }

    suspend fun checkCommentReadAccess(comment: Comment, userId: UUID) {
        checkTaskAccess(userId = userId, taskId = comment.taskId, accessType = AccessType.Read)
    }

    fun checkUserAccess(userId: UUID, targetUserId: UUID) {
        if (userId != targetUserId) throw NoAccessException()
    }
}
