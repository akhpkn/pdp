package com.github.akhpkn.pdp.db

import com.github.akhpkn.pdp.domain.comment.model.Comment
import com.github.akhpkn.pdp.domain.comment.model.CommentData
import com.github.akhpkn.pdp.domain.feedback.model.FeedbackRequest
import com.github.akhpkn.pdp.domain.feedback.model.TaskFeedback
import com.github.akhpkn.pdp.domain.notification.model.NotificationSettings
import com.github.akhpkn.pdp.domain.plan.model.Plan
import com.github.akhpkn.pdp.domain.plan.model.PlanAccess
import com.github.akhpkn.pdp.domain.plan.model.PlanAccessInfo
import com.github.akhpkn.pdp.domain.plan.model.PlanData
import com.github.akhpkn.pdp.domain.task.model.Task
import com.github.akhpkn.pdp.domain.task.model.TaskAuditData
import com.github.akhpkn.pdp.domain.task.model.TaskStatus
import com.github.akhpkn.pdp.domain.user.model.User
import com.github.akhpkn.pdp.domain.user.model.UserCredentials
import com.github.akhpkn.pdp.security.AccessType
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import java.time.Instant
import java.util.UUID

object MappingFunctions {

    private fun <T> mapTo(mapping: Row.() -> T) = { row: Row, _: RowMetadata ->
        mapping(row)
    }

    val toUser = mapTo {
        User(
            id = get("id") as UUID,
            email = get("email") as String,
            name = get("name") as String,
            surname = get("surname") as String
        )
    }

    val toUserCredentials = mapTo {
        UserCredentials(
            userId = get("user_id") as UUID,
            email = get("email") as String,
            password = get("password") as String
        )
    }

    val toPlan = mapTo {
        Plan(
            id = get("id") as UUID,
            title = get("title") as String,
            userId = get("user_id") as UUID,
            createDt = get("create_dt", Instant::class.java)!!,
            dueTo = get("due_to", Instant::class.java)!!
        )
    }

    val toPlanData = mapTo {
        PlanData(
            id = get("id") as UUID,
            title = get("title") as String,
            userId = get("user_id") as UUID,
            userEmail = get("user_email") as String,
            userName = get("user_name") as String,
            userSurname = get("user_surname") as String,
            createDt = get("create_dt", Instant::class.java)!!,
            dueTo = get("due_to", Instant::class.java)!!
        )
    }

    val toPlanAccess = mapTo {
        PlanAccess(
            userId = get("user_id") as UUID,
            planId = get("plan_id") as UUID,
            type = get("type")
                .run { this as String }
                .let { AccessType.valueOf(it) }
        )
    }

    val toPlanAccessInfo = mapTo {
        PlanAccessInfo(
            planId = get("plan_id") as UUID,
            userId = get("user_id") as UUID,
            userEmail = get("user_email") as String,
            userName = get("user_name") as String,
            userSurname = get("user_surname") as String,
            type = get("type")
                .run { this as String }
                .let { AccessType.valueOf(it) }
        )
    }

    val toTask = mapTo {
        Task(
            id = get("id") as UUID,
            title = get("title") as String,
            description = get("description") as String,
            acceptanceCriteria = get("acceptance_criteria") as String,
            planId = get("plan_id") as UUID,
            status = get("status")
                .run { this as String }
                .let { TaskStatus.valueOf(it) },
            createDt = get("create_dt", Instant::class.java)!!,
            dueTo = get("due_to", Instant::class.java)!!
        )
    }

    val toTaskAuditData = mapTo {
        TaskAuditData(
            taskId = get("task_id") as UUID,
            taskTitle = get("task_title") as String,
            status = get("status")
                .run { this as String }
                .let { TaskStatus.valueOf(it) },
            dateTime = get("date_time", Instant::class.java)!!
        )
    }

    val toComment = mapTo {
        Comment(
            id = get("id") as UUID,
            text = get("text") as String,
            taskId = get("task_id") as UUID,
            userId = get("user_id") as UUID,
            createDt = get("create_dt", Instant::class.java)!!,
            updateDt = get("update_dt", Instant::class.java)!!
        )
    }

    val toCommentData = mapTo {
        CommentData(
            id = get("id") as UUID,
            text = get("text") as String,
            userId = get("user_id") as UUID,
            userName = get("user_name") as String,
            userEmail = get("user_email") as String,
            userSurname = get("user_surname") as String,
            createDt = get("create_dt", Instant::class.java)!!
        )
    }

    val toTaskFeedback = mapTo {
        TaskFeedback(
            id = get("id") as UUID,
            requestId = get("request_id") as UUID,
            text = get("text") as String,
            createDt = get("create_dt", Instant::class.java)!!
        )
    }

    val toFeedbackRequest = mapTo {
        FeedbackRequest(
            id = get("id") as UUID,
            requesterId = get("requester_id") as UUID,
            assigneeId = get("assignee_id") as UUID,
            taskId = get("task_id") as UUID,
            createdDt = get("create_dt", Instant::class.java)!!
        )
    }

    val toNotificationSettings = mapTo {
        NotificationSettings(
            userId = get("user_id") as UUID,
            enabled = get("enabled") as Boolean,
            daysBeforeDeadline = get("days_before_deadline") as Int,
            daysBeforeReport = get("days_before_report") as Int
        )
    }
}
