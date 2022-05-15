package com.github.akhpkn.pdp.db

import com.github.akhpkn.pdp.domain.comment.dao.CommentDao
import com.github.akhpkn.pdp.domain.comment.model.Comment
import com.github.akhpkn.pdp.domain.comment.model.CommentData
import kotlinx.coroutines.flow.Flow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.awaitSingleOrNull
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
class CommentDaoImpl(private val databaseClient: DatabaseClient) : CommentDao {

    override suspend fun find(id: UUID): Comment? = run {
        databaseClient
            .sql("select * from comment where id=:id")
            .bind("id", id)
            .map(MappingFunctions.toComment)
            .awaitSingleOrNull()
    }

    override fun listByTask(taskId: UUID): Flow<Comment> = run {
        databaseClient
            .sql("select * from comment where task_id=:taskId")
            .bind("taskId", taskId)
            .map(MappingFunctions.toComment)
            .flow()
    }

    override fun listByTaskV2(taskId: UUID): Flow<CommentData> = run {
        databaseClient
            .sql(
                """
                    |select c.id as id, c.text as text, c.create_dt as create_dt,
                    |u.id as user_id, u.name as user_name, u.surname as user_surname, u.email as user_email
                    |from comment c 
                    |join "user" u on c.user_id = u.id
                    |where task_id=:taskId
                    |order by create_dt
                """.trimMargin()
            )
            .bind("taskId", taskId)
            .map(MappingFunctions.toCommentData)
            .flow()
    }

    override suspend fun insert(comment: Comment) {
        databaseClient
            .sql(
                "insert into comment(id, text, task_id, user_id, create_dt, update_dt)" +
                    " values (:id, :text, :taskId, :userId, :createDt, :updateDt)"
            )
            .bind("id", comment.id)
            .bind("text", comment.text)
            .bind("taskId", comment.taskId)
            .bind("userId", comment.userId)
            .bind("createDt", comment.createDt)
            .bind("updateDt", comment.updateDt)
            .await()
    }

    override suspend fun update(id: UUID, text: String) {
        databaseClient
            .sql("update comment set text=:text, update_dt=:updateDt where id=:id")
            .bind("id", id)
            .bind("text", text)
            .bind("updateDt", Instant.now())
            .await()
    }

    override suspend fun delete(id: UUID) {
        databaseClient
            .sql("delete from comment where id=:id")
            .bind("id", id)
            .await()
    }
}
