package com.github.akhpkn.pdp.db

import com.github.akhpkn.pdp.domain.feedback.dao.FeedbackDao
import com.github.akhpkn.pdp.domain.feedback.model.FeedbackRequest
import com.github.akhpkn.pdp.domain.feedback.model.TaskFeedback
import com.github.akhpkn.pdp.domain.feedback.model.TaskFeedbackView
import kotlinx.coroutines.flow.Flow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.awaitSingleOrNull
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
class FeedbackDaoImpl(private val databaseClient: DatabaseClient) : FeedbackDao {

    override suspend fun find(id: UUID): TaskFeedback? = run {
        databaseClient
            .sql("select * from task_feedback where id=:id")
            .bind("id", id)
            .map(MappingFunctions.toTaskFeedback)
            .awaitSingleOrNull()
    }

    override suspend fun findByRequest(requestId: UUID): TaskFeedback? = run {
        databaseClient
            .sql("select * from task_feedback where request_id=:requestId")
            .bind("requestId", requestId)
            .map(MappingFunctions.toTaskFeedback)
            .awaitSingleOrNull()
    }

    override fun list(taskId: UUID): Flow<TaskFeedbackView> = run {
        databaseClient
            .sql(
                """
                    select f.id, f.text, f.create_dt, u.id as user_id, u.email, u.name, u.surname
                    from task_feedback f
                    join feedback_request r on f.request_id=r.id
                    join "user" u on r.assignee_id=u.id
                    where r.task_id=:taskId 
                    order by f.create_dt
                """.trimIndent()
            )
            .bind("taskId", taskId)
            .map { row, _ ->
                TaskFeedbackView(
                    id = row.get("id") as UUID,
                    text = row.get("text") as String,
                    createDt = row.get("create_dt", Instant::class.java)!!,
                    authorId = row.get("user_id") as UUID,
                    authorEmail = row.get("email") as String,
                    authorName = row.get("name") as String,
                    authorSurname = row.get("surname") as String
                )
            }
            .flow()
    }

    override suspend fun insert(feedback: TaskFeedback) {
        databaseClient
            .sql("insert into task_feedback(id, request_id, text, create_dt) values(:id, :requestId, :text, :createDt)")
            .bind("id", feedback.id)
            .bind("requestId", feedback.requestId)
            .bind("text", feedback.text)
            .bind("createDt", feedback.createDt)
            .await()
    }

    override suspend fun insert(request: FeedbackRequest) {
        databaseClient
            .sql("insert into feedback_request(id, requester_id, assignee_id, task_id, create_dt) values (:id, :requesterId, :assigneeId, :taskId, :createDt)")
            .bind("id", request.id)
            .bind("requesterId", request.requesterId)
            .bind("assigneeId", request.assigneeId)
            .bind("taskId", request.taskId)
            .bind("createDt", request.createdDt)
            .await()
    }

    override suspend fun findRequest(id: UUID): FeedbackRequest? = run {
        databaseClient
            .sql("select * from feedback_request where id=:id")
            .bind("id", id)
            .map(MappingFunctions.toFeedbackRequest)
            .awaitSingleOrNull()
    }

    override suspend fun findRequest(taskId: UUID, assigneeId: UUID): FeedbackRequest? = run {
        databaseClient
            .sql("select * from feedback_request where task_id=:taskId and assignee_id=:assigneeId")
            .bind("taskId", taskId)
            .bind("assigneeId", assigneeId)
            .map(MappingFunctions.toFeedbackRequest)
            .awaitSingleOrNull()
    }

    override suspend fun listRequest(taskId: UUID, assigneeId: UUID): Flow<FeedbackRequest> = run {
        databaseClient
            .sql("select * from feedback_request where task_id=:taskId and assignee_id=:assigneeId")
            .bind("taskId", taskId)
            .bind("assigneeId", assigneeId)
            .map(MappingFunctions.toFeedbackRequest)
            .flow()
    }
}
