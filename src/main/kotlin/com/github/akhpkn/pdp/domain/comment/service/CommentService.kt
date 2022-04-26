package com.github.akhpkn.pdp.domain.comment.service

import com.github.akhpkn.pdp.domain.comment.dao.CommentDao
import com.github.akhpkn.pdp.domain.comment.model.Comment
import com.github.akhpkn.pdp.domain.comment.model.CommentData
import com.github.akhpkn.pdp.exception.NoAccessException
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CommentService(private val dao: CommentDao) {

    suspend fun getById(id: UUID): Comment = dao.find(id) ?: throw RuntimeException("Comment not found")

    fun getByTaskId(taskId: UUID): Flow<Comment> {
        return dao.listByTask(taskId)
    }

    fun getByTaskIdV2(taskId: UUID): Flow<CommentData> {
        return dao.listByTaskV2(taskId)
    }

    suspend fun create(userId: UUID, taskId: UUID, text: String) {
        val comment = Comment.new(text, userId, taskId)
        dao.insert(comment)
    }

    suspend fun update(id: UUID, text: String, userId: UUID) {
        val comment = getById(id)
        if (comment.userId != userId) throw NoAccessException()
        dao.update(comment.id, text)
    }

    suspend fun delete(id: UUID, userId: UUID) {
        val comment = getById(id)
        if (comment.userId != userId) throw NoAccessException()
        dao.delete(comment.id)
    }
}
