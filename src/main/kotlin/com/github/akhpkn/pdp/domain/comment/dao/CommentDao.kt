package com.github.akhpkn.pdp.domain.comment.dao

import com.github.akhpkn.pdp.domain.comment.model.Comment
import com.github.akhpkn.pdp.domain.comment.model.CommentData
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface CommentDao {

    suspend fun find(id: UUID): Comment?

    fun listByTask(taskId: UUID): Flow<Comment>

    fun listByTaskV2(taskId: UUID): Flow<CommentData>

    suspend fun insert(comment: Comment)

    suspend fun update(id: UUID, text: String)

    suspend fun delete(id: UUID)
}
